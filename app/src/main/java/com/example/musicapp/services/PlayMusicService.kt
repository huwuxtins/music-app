package com.example.musicapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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

class PlayMusicService: Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private var mMediaPlayer: MediaPlayer? = null
    private var handler: Handler? = null
    private val sharedPreferencesKey = "last_played_position"

    private val channelId = "YOUR_CHANNEL_ID"
    private val notificationId = 100

    private var song: Song? = null


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
                }
            }

            handler?.postDelayed(this, 1000) // Update every 1000 milliseconds (1 second)
        }
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("MyApp", "Intent's action: ${intent?.action}")
            if(intent?.action == "UPDATE_MUSIC"){
                val duration = intent.getIntExtra("currentDuration", 0)
                saveLastPlayedPosition(duration)
                mMediaPlayer?.seekTo(duration)
            }
            else if(intent?.action == "PAUSE_MUSIC"){
                mMediaPlayer?.pause()
                updateNotification(getNotification("STATUS_PAUSE"))
            }
            else if(intent?.action == "PLAY_MUSIC"){
                mMediaPlayer?.start()
                updateNotification(getNotification("STATUS_PLAY"))

            }
            else if(intent?.action == "PREV_MUSIC"){

            }
            else if(intent?.action == "NEXT_MUSIC"){

            }
            else if(intent?.action == "CLOSE_MUSIC"){
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        song = intent?.extras?.getSerializable("song") as? Song

        val notification = getNotification("STATUS_PLAY")

        val channel = NotificationChannel(
            channelId,
            "Music Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        when(intent?.action) {
            "MUSIC_PAUSE" -> {
                mMediaPlayer?.pause()
                updateNotification(getNotification("STATUS_PAUSE"))
                return START_STICKY
            }

            "MUSIC_RESUME" -> {
                mMediaPlayer?.start()
                Log.e("MyApp", "MUSIC_RESUME")
                updateNotification(getNotification("STATUS_PLAY"))
                return START_STICKY
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

                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putLong("song_id", song!!.id).apply()

                    val storageRef: StorageReference = storage.reference.child(song!!.link)

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

        val updateFilter = IntentFilter("UPDATE_MUSIC")
        registerReceiver(receiver, updateFilter)
        val pauseFilter = IntentFilter("PAUSE_MUSIC")
        registerReceiver(receiver, pauseFilter)
        val playFilter = IntentFilter("PLAY_MUSIC")
        registerReceiver(receiver, playFilter)
        val closeFilter = IntentFilter("CLOSE_MUSIC")
        registerReceiver(receiver, closeFilter)
    }

    private fun updateNotification(notification: Notification) {
        Log.e("MyApp", "Updating notification")
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(notificationId, notification)
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

    fun getNotification(status: String): Notification{
        val mediaSessionCompat = MediaSessionCompat(this, "tag")

        val pauseIntent = Intent()
        pauseIntent.action = "PAUSE_MUSIC"
        val pendingIntentPause = PendingIntent.getBroadcast(this, 12345, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent()
        playIntent.action = "PLAY_MUSIC"
        val pendingIntentPlay = PendingIntent.getBroadcast(this, 12345, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val closeIntent = Intent()
        closeIntent.action = "CLOSE_MUSIC"
        val pendingIntentClose = PendingIntent.getBroadcast(this, 12345, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        if(status == "STATUS_PLAY"){
            return  NotificationCompat.Builder(this, channelId)
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.logo_music_app)
                // Add media control buttons that invoke intents in your media service
                .addAction(R.drawable.icon_pre, "Previous", null) // #0
                .addAction(R.drawable.icon_pause, "Pause", pendingIntentPause) // #1
                .addAction(R.drawable.icon_next, "Next", null) // #2
                .addAction(R.drawable.icon_close, "Close", pendingIntentClose) //  #3
                // Apply the media style template.
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1 /* #1: pause button \*/)
                    .setMediaSession(mediaSessionCompat.sessionToken))
                .setContentTitle(song?.name)
                .build()
        }
        return NotificationCompat.Builder(this, channelId)
            // Show controls on lock screen even when user hides sensitive content.
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.logo_music_app)
            // Add media control buttons that invoke intents in your media service
            .addAction(R.drawable.icon_pre, "Previous", null) // #0
            .addAction(R.drawable.icon_play, "Play", pendingIntentPlay) // #1
            .addAction(R.drawable.icon_next, "Next", null) // #2
            .addAction(R.drawable.icon_close, "Close", pendingIntentClose) // #3
            // Apply the media style template.
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1 /* #1: pause button \*/)
                .setMediaSession(mediaSessionCompat.sessionToken))
            .setContentTitle(song?.name)
            .build()
    }
}