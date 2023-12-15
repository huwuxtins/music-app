package com.example.musicapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.musicapp.R
import com.example.musicapp.models.Song
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PlayMusicService(): Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private var mMediaPlayer: MediaPlayer? = null
    private var handler: Handler? = null
    private val sharedPreferencesKey = "last_played_position"

    private val updateSeekBarRunnable: Runnable = object : Runnable {
        override fun run() {
            mMediaPlayer?.let { mediaPlayer ->
                try {
                    if (mediaPlayer.isPlaying) {
                        val currentPosition = mediaPlayer.currentPosition
                        sendBroadcast(Intent("com.example.updateSeekBar")
                            .putExtra("currentPosition", currentPosition)
                            .putExtra("max", mediaPlayer.duration))

                        saveLastPlayedPosition(mediaPlayer.currentPosition)
                    } else {
                        Log.e("MyApp", "Media is stopping")
                    }
                } catch (e: Exception) {
                    // Handle the IllegalStateException (e.g., log the error)
//                    Log.e("MyApp", "Error checking if MediaPlayer is playing: ${e.message}")
                }
            }

            handler?.postDelayed(this, 1000) // Update every 1000 milliseconds (1 second)
        }
    }


    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action == "com.example.updateMedia"){
                val duration = intent.getIntExtra("currentDuration", 0)
                Log.e("MyApp", "Duration of change seekbar: $duration")
                saveLastPlayedPosition(duration)
                mMediaPlayer?.seekTo(duration)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val song = intent?.extras?.getSerializable("song") as? Song
        val channelId = "YOUR_CHANNEL_ID"
        val notificationId = 100

        val mediaSessionCompat = MediaSessionCompat(this, "tag")

        val notification = NotificationCompat.Builder(this, channelId)
            // Show controls on lock screen even when user hides sensitive content.
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.logo_music_app)
            // Add media control buttons that invoke intents in your media service
            .addAction(R.drawable.icon_pre, "Previous", null) // #0
            .addAction(R.drawable.icon_pause, "Pause", null) // #1
            .addAction(R.drawable.icon_next, "Next", null) // #2
            // Apply the media style template.
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1 /* #1: pause button \*/)
                .setMediaSession(mediaSessionCompat.sessionToken))
            .setContentTitle("Music App")
            .setContentText(song?.name)
            .build()

//      Create a notification channel (for Android Oreo and above)
        val channel = NotificationChannel(
            channelId,
            "Music Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        when(intent?.action) {
            "MUSIC_STOP" -> {
                mMediaPlayer?.stop()
            }

            "MUSIC_PLAY", "NEW_MUSIC_PLAY" -> {
                mMediaPlayer?.let { mediaPlayer ->
                    if(mediaPlayer.isPlaying){
                        mediaPlayer.release()
                    }
                }
                if(song != null){
                    if(intent.action == "NEW_MUSIC_PLAY"){
                        saveLastPlayedPosition(0)
                    }
                    val storage = FirebaseStorage.getInstance()
                    val storageRef: StorageReference = storage.reference.child(song.link)
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        mMediaPlayer = MediaPlayer()
                        mMediaPlayer?.apply {
                            setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                            )
                            setDataSource(applicationContext, uri)
                            setOnPreparedListener(this@PlayMusicService)
                            prepareAsync()

                        }
                    }
                }
            }

            "MUSIC_DESTROY" -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        ServiceCompat.startForeground(this, notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        return START_STICKY
    }
    override fun onPrepared(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
        val lastPlayedPosition = getLastPlayedPosition()
        mediaPlayer.seekTo(lastPlayedPosition)

        handler = Handler()
        handler?.post(updateSeekBarRunnable)

        val filter = IntentFilter("com.example.updateMedia")
        registerReceiver(receiver, filter)
    }

    private fun saveLastPlayedPosition(position: Int) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(sharedPreferencesKey, position).apply()
    }

    private fun getLastPlayedPosition(): Int {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt(sharedPreferencesKey, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        mMediaPlayer?.release()
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        handler?.removeCallbacks(updateSeekBarRunnable)
        // ... (existing code)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}