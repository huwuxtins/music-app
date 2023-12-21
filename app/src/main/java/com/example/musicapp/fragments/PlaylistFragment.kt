package com.example.musicapp.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.adapters.NewSongAdapter
import com.example.musicapp.models.Playlist
import com.example.musicapp.services.PlayMusicService
import com.example.musicapp.utils.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PlaylistFragment(val playlist: Playlist) : Fragment(R.layout.fragment_playlist) {
        private lateinit var imgAvatar: ImageView
        private lateinit var tvName: TextView
        private lateinit var btnDownload: ImageButton
        private lateinit var btnRandom: ImageButton
        private lateinit var btnPlay: ImageButton
        private lateinit var btnPlus: ImageButton
        private lateinit var rcvSong: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)

        imgAvatar = view.findViewById(R.id.imgAvatar)
        tvName = view.findViewById(R.id.tvName)
        btnDownload = view.findViewById(R.id.btnDownload)
        btnRandom = view.findViewById(R.id.btnRandom)
        btnPlay = view.findViewById(R.id.btnPlay)
        btnPlus = view.findViewById(R.id.btnPlus)
        rcvSong = view.findViewById(R.id.rcvSong)

        val mainActivity = context as MainActivity
        tvName.text = playlist.name

        rcvSong.adapter = context?.let { NewSongAdapter(it, playlist.songs, false, playlist) }
        rcvSong.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)

        btnDownload.setOnClickListener{
            Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show()
            val sharedPreferences = view.context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            for(song in playlist.songs){
                song.isDownloaded = true
                sharedPreferences.edit()
                    .putString(
                        "downloaded_song_${song.id}",
                        "${song.id}_${song.name}_${song.type}_${song.lyric}_${song.postAt}_${song.artist}_${song.artistName}_${song.isLoved}_${song.isDownloaded}")
                    .apply()

                val storage = FirebaseStorage.getInstance()
                val storageRef: StorageReference = storage.reference.child(song.link)

                context as MainActivity
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    when {
                        ContextCompat.checkSelfPermission(
                            view.context,
                            Manifest.permission.ACCESS_MEDIA_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            // You can use the API that requires the permission.
                            val fileDownloadTask = FileDownloadTask(song.id.toString(), playlist.name)
                            fileDownloadTask.execute(uri.toString())
                            Toast.makeText(context, "Downloaded", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                        }
                    }
                }
            }
        }

        btnRandom.setOnClickListener{
            try {
                val randomList = playlist.songs.shuffled()
                mainActivity.loadFragment(SongFragment(randomList[0], ArrayList(randomList)), "body")
                val intent = Intent(view.context, PlayMusicService::class.java)
                intent.putExtra("song", randomList[0])
                intent.putExtra("songs", ArrayList(randomList))
                intent.action = "NEW_MUSIC_PLAY"
                mainActivity.startForegroundService(intent)
            }
            catch(e: Exception) {
                Toast.makeText(context, "Don't have any songs in this playlist!", Toast.LENGTH_LONG).show()
            }
        }

        btnPlay.setOnClickListener{
            try {
                mainActivity.loadFragment(SongFragment(playlist.songs[0], playlist.songs), "body")
                val intent = Intent(view.context, PlayMusicService::class.java)
                intent.putExtra("song", playlist.songs[0])
                intent.putExtra("songs", playlist.songs)
                intent.action = "NEW_MUSIC_PLAY"
                mainActivity.startForegroundService(intent)
            }
            catch(e: Exception) {
                Toast.makeText(context, "Don't have any songs in this playlist!", Toast.LENGTH_LONG).show()
            }
        }

        btnPlus.setOnClickListener{
            mainActivity.loadFragment(SearchFragment(), "main")
        }
        return view
    }
}
