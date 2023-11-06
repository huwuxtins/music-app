package com.example.musicapp.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.models.Song
import java.util.Timer
import java.util.TimerTask

class TrackFragment(private val song: Song): Fragment(R.layout.fragment_track) {
    private lateinit var imgSong: ImageView
    private lateinit var tvNameSong: TextView
    private lateinit var tvNameArtists: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_track, container, false)
        imgSong = view.findViewById(R.id.imgSong)
        tvNameSong = view.findViewById(R.id.tvNameSong)
        tvNameArtists = view.findViewById(R.id.tvNameArtists)

        tvNameSong.text = song.name
        tvNameArtists.text = song.getArtists()
        imgSong.setImageDrawable(context?.resources?.getDrawable(R.drawable.alan_walker, null))

        return view
    }
}