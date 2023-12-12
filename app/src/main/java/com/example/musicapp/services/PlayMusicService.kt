package com.example.musicapp.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.musicapp.models.Song

class PlayMusicService(): Service() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, "CHANNEL_IxD")
            // Create the notification to display while the service is running
            .build()
        val song = intent?.extras?.getSerializable("song", Song::class.java)
        Log.e("MyApp", song.toString())
        return START_STICKY
    }



    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}