package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.adapters.NewSongAdapter
import com.example.musicapp.models.Playlist

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

        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvName = view.findViewById(R.id.tvName)
        btnDownload = view.findViewById(R.id.btnDownload)
        btnRandom = view.findViewById(R.id.btnRandom)
        btnPlay = view.findViewById(R.id.btnPlay)
        btnPlus = view.findViewById(R.id.btnPlus)
        rcvSong = view.findViewById(R.id.rcvSong)

        val mainActivity = context as MainActivity
        tvName.text = playlist.name

        rcvSong.adapter = context?.let { NewSongAdapter(it, playlist.songs) }
        rcvSong.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        btnRandom.setOnClickListener{
            try {
                val randomList = playlist.songs.shuffled()
                mainActivity.loadFragment(SongFragment(randomList[0], ArrayList(randomList)), "body")
            }
            catch(e: Exception) {
                Toast.makeText(context, "Don't have any songs in this playlist!", Toast.LENGTH_LONG).show()
            }
        }
        return view
    }
}
