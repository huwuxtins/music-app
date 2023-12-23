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
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.models.Song
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File

class PlayMusicService: Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private var mMediaPlayer: MediaPlayer? = null
    private var handler: Handler? = null
    private val sharedPreferencesKey = "last_played_position"

    private val channelId = "YOUR_CHANNEL_ID"
    private val notificationId = 100

    private var song: Song? = null
    private var songs: ArrayList<Song>? = null
    private val storage = FirebaseStorage.getInstance()

    private val updateSeekBarRunnable: Runnable = object : Runnable {
        override fun run() {
            mMediaPlayer?.let { mediaPlayer ->
                try {
                    if (mediaPlayer.isPlaying) {
                        val currentPosition = mediaPlayer.currentPosition
                        sendBroadcast(Intent("com.example.UPDATE_SEEKBAR")
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
            when(intent?.action){
                "UPDATE_MUSIC" -> {
                    val duration = intent.getIntExtra("currentDuration", 0)
                    saveLastPlayedPosition(duration)
                    mMediaPlayer?.seekTo(duration)
                }
                "PAUSE_MUSIC" -> {
                    mMediaPlayer?.pause()
                    getNotification("STATUS_PAUSE", null)
                    sendBroadcast(Intent("com.example.PAUSE_MUSIC")
                        .putExtra("status", "next"))
                }
                "PLAY_MUSIC" -> {
                    mMediaPlayer?.start()
                    getNotification("STATUS_PLAY", null)
                    sendBroadcast(Intent("com.example.PLAY_MUSIC")
                        .putExtra("status", "next"))
                }
                "NEXT_MUSIC" ->{
                    if(songs != null){
                        val positionOfCurrentSong = songs!!.indexOf(songs!!.find { s -> s.id == song!!.id })
                        var positionOfNextSong = 0
                        if(positionOfCurrentSong < songs!!.size - 1){
                            positionOfNextSong = positionOfCurrentSong + 1
                        }
                        song = songs!![positionOfNextSong]
                        mMediaPlayer?.let { mediaPlayer ->
                            if(mediaPlayer.isPlaying){
                                mediaPlayer.release()
                            }
                        }
                        saveLastPlayedPosition(0)

                        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putLong("song_id", song!!.id).apply()

                        playMusic(song!!)
                    }
                    getNotification("STATUS_PLAY", null)
                    sendBroadcast(Intent("com.example.CHANGE_MUSIC")
                        .putExtra("status", "next"))
                }
                "PRE_MUSIC" -> {
                    if(songs != null){
                        val positionOfCurrentSong = songs!!.indexOf(songs!!.find { s -> s.id == song!!.id })
                        var positionOfPreSong = 0
                        if(positionOfCurrentSong > 0){
                            positionOfPreSong = positionOfCurrentSong -1
                        }
                        song = songs!![positionOfPreSong]
                        mMediaPlayer?.let { mediaPlayer ->
                            if(mediaPlayer.isPlaying){
                                mediaPlayer.release()
                            }
                        }
                        saveLastPlayedPosition(0)

                        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putLong("song_id", song!!.id).apply()

                        playMusic(song!!)
                    }
                    getNotification("STATUS_PLAY", null)
                    sendBroadcast(Intent("com.example.CHANGE_MUSIC")
                        .putExtra("status", "pre"))
                }
                "CLOSE_MUSIC" -> {
                    val sharedPreferences = this@PlayMusicService.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().remove("song_id").apply()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        song = intent?.extras?.getSerializable("song") as? Song
        songs = intent?.extras?.getSerializable("songs") as? ArrayList<Song>
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
                getNotification("STATUS_PAUSE", null)
                return START_STICKY
            }
            "MUSIC_RESUME" -> {
                mMediaPlayer?.start()
                Log.e("MyApp", "MUSIC_RESUME")
                getNotification("STATUS_PLAY", null)
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
                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putLong("song_id", song!!.id).apply()

                    playMusic(song!!)
                }
            }
            "MUSIC_LOOP" -> {
                if(mMediaPlayer?.isLooping == true){
                    mMediaPlayer?.isLooping = false
                    Log.e("MyApp", "The end of the song: ${mMediaPlayer?.isLooping}")
                }
                else{
                    mMediaPlayer?.isLooping = true
                    Log.e("MyApp", "The end of the song: ${mMediaPlayer?.isLooping}")
                }
            }

            "MUSIC_DESTROY" -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        getNotification("STATUS_PLAY") {
            ServiceCompat.startForeground(
                this,
                notificationId,
                it,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        }

        return START_STICKY
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
        val lastPlayedPosition = getLastPlayedPosition()
        mediaPlayer.seekTo(lastPlayedPosition)

        mediaPlayer.setOnCompletionListener {
            if(it.isLooping){
                it.start()
            }
            else{
                if(songs != null){
                    val positionOfCurrentSong = songs!!.indexOf(songs!!.find { s -> s.id == song!!.id })
                    var positionOfNextSong = 0
                    if(positionOfCurrentSong < songs!!.size - 1){
                        positionOfNextSong = positionOfCurrentSong + 1
                    }
                    song = songs!![positionOfNextSong]
                    mMediaPlayer?.let { mediaPlayer ->
                        if(mediaPlayer.isPlaying){
                            mediaPlayer.release()
                        }
                    }
                    saveLastPlayedPosition(0)

                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putLong("song_id", song!!.id).apply()

                    playMusic(song!!)
                }
                else{
                    Log.e("MyApp", "Songs is null")
                }
                getNotification("STATUS_PLAY", null)
                sendBroadcast(Intent("com.example.CHANGE_MUSIC")
                    .putExtra("status", "next"))
            }
        }

        handler = Handler()
        handler?.post(updateSeekBarRunnable)

        val updateFilter = IntentFilter("UPDATE_MUSIC")
        registerReceiver(receiver, updateFilter)
        val homeFilter = IntentFilter("HOME_MUSIC")
        registerReceiver(receiver, homeFilter)
        val pauseFilter = IntentFilter("PAUSE_MUSIC")
        registerReceiver(receiver, pauseFilter)
        val playFilter = IntentFilter("PLAY_MUSIC")
        registerReceiver(receiver, playFilter)
        val nextFilter = IntentFilter("NEXT_MUSIC")
        registerReceiver(receiver, nextFilter)
        val preFilter = IntentFilter("PRE_MUSIC")
        registerReceiver(receiver, preFilter)
        val closeFilter = IntentFilter("CLOSE_MUSIC")
        registerReceiver(receiver, closeFilter)
    }

    private fun playMusic(song: Song){
        if(song.isDownloaded){
            mMediaPlayer = MediaPlayer()
            mMediaPlayer?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                Log.e("MyApp", "File's name: ${File(song.link).name}")
                setDataSource(this@PlayMusicService, Uri.fromFile(File(song.link)))
                setOnPreparedListener(this@PlayMusicService)
                prepareAsync()

            }
        }
        else{
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
                    setDataSource(this@PlayMusicService, uri)
                    setOnPreparedListener(this@PlayMusicService)
                    prepareAsync()

                }
            }
        }
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
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    fun getNotification(status: String, onComplete: ((Notification) -> Unit)?){
        val mediaSessionCompat = MediaSessionCompat(this, "tag")

        val homeIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        homeIntent.action = "HOME_MUSIC"
        homeIntent.putExtra("song", song)
        homeIntent.putExtra("songs", songs)
        val pendingIntentHome = PendingIntent.getActivity(this, 12345, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val pauseIntent = Intent()
        pauseIntent.action = "PAUSE_MUSIC"
        val pendingIntentPause = PendingIntent.getBroadcast(this, 12345, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent()
        playIntent.action = "PLAY_MUSIC"
        val pendingIntentPlay = PendingIntent.getBroadcast(this, 12345, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent()
        nextIntent.action = "NEXT_MUSIC"
        val pendingIntentNext = PendingIntent.getBroadcast(this, 12345, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val preIntent = Intent()
        preIntent.action = "PRE_MUSIC"
        val pendingIntentPre = PendingIntent.getBroadcast(this, 12345, preIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val closeIntent = Intent()
        closeIntent.action = "CLOSE_MUSIC"
        val pendingIntentClose = PendingIntent.getBroadcast(this, 12345, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, channelId)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.logo_music_app)
            .setContentIntent(pendingIntentHome)
            // Apply the media style template.
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1 /* #1: pause button \*/)
                .setMediaSession(mediaSessionCompat.sessionToken))
            .setContentTitle(song?.name)
            .addAction(R.drawable.icon_pre, "Previous", pendingIntentPre) // #0
        if(status == "STATUS_PLAY"){

            notification
                .addAction(R.drawable.icon_pause, "Pause", pendingIntentPause) // #1
        }
        else{
            notification
                .addAction(R.drawable.icon_play, "Play", pendingIntentPlay) // #1
        }
        if(song != null){
            if(!song?.isDownloaded!!){

                Picasso.get().load(song?.image).into(object: Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

                        val notificationManager = getSystemService(NotificationManager::class.java)
                        val notificationBuilder = notification
                            .addAction(R.drawable.icon_next, "Next", pendingIntentNext) // #2
                            .addAction(R.drawable.icon_close, "Close", pendingIntentClose) //  #3
                            .setLargeIcon(bitmap).build()
                        notificationManager.notify(notificationId, notificationBuilder)
                        if (onComplete != null) {
                            onComplete(notificationBuilder)
                        }
                    }

                    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }

                })
            }
            else{
                val notificationManager = getSystemService(NotificationManager::class.java)
                val notificationBuilder = notification
                    .addAction(R.drawable.icon_next, "Next", pendingIntentNext) // #2
                    .addAction(R.drawable.icon_close, "Close", pendingIntentClose) //  #3
                    .build()
                notificationManager.notify(notificationId, notificationBuilder)
                if (onComplete != null) {
                    onComplete(notificationBuilder)
                }
            }
        }
    }
}