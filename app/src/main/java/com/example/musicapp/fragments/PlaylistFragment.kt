package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.adapters.SongAdapter
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Playlist
import com.example.musicapp.models.Song
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class PlaylistFragment(val playlist: Playlist) : Fragment(R.layout.fragment_playlist) {
        private lateinit var imgAvatar: ImageView
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

        imgAvatar = view.findViewById(R.id.imgAvatar);
        btnDownload = view.findViewById(R.id.btnDownload)
        btnRandom = view.findViewById(R.id.btnRandom)
        btnPlay = view.findViewById(R.id.btnPlay)
        btnPlus = view.findViewById(R.id.btnPlus)
        rcvSong = view.findViewById(R.id.rcvSong)

        val mainActivity = context as MainActivity

        rcvSong.adapter = context?.let { SongAdapter(it, playlist.songs) }
        rcvSong.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        btnRandom.setOnClickListener{
            val randomList = playlist.songs.shuffled()
            mainActivity.loadFragment(SongFragment(randomList[0], ArrayList(randomList)), "body")
        }
        return view
    }
}
