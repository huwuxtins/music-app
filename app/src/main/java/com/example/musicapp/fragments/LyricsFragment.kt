package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.example.musicapp.models.Song

class LyricsFragment(val song: Song): Fragment(R.layout.fragment_lyrics) {
    private lateinit var imgSong: ImageView
    private lateinit var tvNameSong: TextView
    private lateinit var tvNameArtists: TextView
    private lateinit var tvLyrics: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lyrics, container, false)
        imgSong = view.findViewById(R.id.imgSong)
        tvNameSong = view.findViewById(R.id.tvNameSong)
        tvNameArtists = view.findViewById(R.id.tvNameArtists)
        tvLyrics = view.findViewById(R.id.tvLyrics)
        imgSong.setImageDrawable(context?.resources?.getDrawable(R.drawable.alan_walker, null))
        return view
    }
}