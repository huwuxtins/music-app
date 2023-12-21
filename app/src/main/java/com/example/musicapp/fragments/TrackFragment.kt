package com.example.musicapp.fragments

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Song
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class TrackFragment(private val song: Song): Fragment(R.layout.fragment_track) {
    private lateinit var imgSong: ImageView
    private lateinit var tvNameSong: TextView
    private lateinit var tvNameArtists: TextView
    private lateinit var sbVol: SeekBar
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_track, container, false)
        imgSong = view.findViewById(R.id.imgSong)
        tvNameSong = view.findViewById(R.id.tvNameSong)
        tvNameArtists = view.findViewById(R.id.tvNameArtists)
        sbVol = view.findViewById(R.id.sb_vol)

        tvNameSong.text = song.name
        tvNameArtists.text = song.artistName
        val audioManager = view.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sbVol.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        sbVol.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        sbVol.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, newVolume: Int, b: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        if(!song.isDownloaded){
            Picasso.get().load(song.image).into(imgSong)
        }

        return view
    }
}