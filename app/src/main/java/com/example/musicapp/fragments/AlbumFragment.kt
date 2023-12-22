package com.example.musicapp.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import com.example.musicapp.models.Album
import com.example.musicapp.services.PlayMusicService
import com.example.musicapp.utils.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AlbumFragment(private val album: Album): Fragment(R.layout.fragment_album) {
    private lateinit var imgAlbum: ImageView
    private lateinit var imgArtist: CircleImageView

    private lateinit var tvAlbumName: TextView
    private lateinit var tvArtistName: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvTimePost: TextView
    private lateinit var tvSongNumber: TextView

    private lateinit var btnDownload: ImageButton
    private lateinit var btnRandom: ImageButton
    private lateinit var btnPlay: ImageButton

    private lateinit var rcvSong: RecyclerView

//    private val
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_album, container, false)
        imgAlbum = view.findViewById(R.id.imgAlbum)
        imgArtist = view.findViewById(R.id.profile_image)
        tvAlbumName = view.findViewById(R.id.tvName)
        tvArtistName = view.findViewById(R.id.tvNameArtist)
        tvYear = view.findViewById(R.id.tvYear)
        tvTimePost = view.findViewById(R.id.tv_time_post)
        tvSongNumber = view.findViewById(R.id.tv_song_number)
        btnDownload = view.findViewById(R.id.btnDownload)
        btnRandom = view.findViewById(R.id.btnRandom)
        btnPlay = view.findViewById(R.id.btnPlay)
        rcvSong = view.findViewById(R.id.rcvSong)

        try {
            Picasso.get().load(album.image).into(imgAlbum)
            Picasso.get().load(album.artistImage).into(imgArtist)
        }
        catch(e: Exception){
            Log.e("MyApp", "You're offline, error: $e")
        }

        tvAlbumName.text = album.name
        tvArtistName.text = album.artistName
        tvYear.text = "Album - ${album.postYear}"
        tvTimePost.text = "Phát hành: ${album.postYear}"
        tvSongNumber.text = "${album.listSong?.size} bài hát"

        rcvSong.adapter = NewSongAdapter(view.context, album.songs, false, null)
        rcvSong.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)

        val mainActivity = context as MainActivity
        btnDownload.setOnClickListener{
            Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show()
            val sharedPreferences = view.context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            for(song in album.songs){
                song.isDownloaded = true
                sharedPreferences.edit()
                    .putString(
                        "downloaded_song_${song.id}",
                        "${song.id}_${song.name}_${song.type}_${song.lyric}_${song.postAt}_${song.artist}_${song.artistName}_${song.isLoved}_${song.isDownloaded}")
                    .apply()

                val storage = FirebaseStorage.getInstance()
                val storageRef: StorageReference = storage.reference.child(song.link)

                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    when {
                        ContextCompat.checkSelfPermission(
                            view.context,
                            Manifest.permission.ACCESS_MEDIA_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            // You can use the API that requires the permission.
                            val fileDownloadTask = FileDownloadTask(song.id.toString(), "Albums/${album.name}")
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
                val randomList = album.songs.shuffled()
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
                mainActivity.loadFragment(SongFragment(album.songs[0], album.songs), "body")
                val intent = Intent(view.context, PlayMusicService::class.java)
                intent.putExtra("song", album.songs[0])
                intent.putExtra("songs", album.songs)
                intent.action = "NEW_MUSIC_PLAY"
                mainActivity.startForegroundService(intent)
            }
            catch(e: Exception) {
                Toast.makeText(context, "Don't have any songs in this playlist!", Toast.LENGTH_LONG).show()
            }
        }
        return view
    }
}