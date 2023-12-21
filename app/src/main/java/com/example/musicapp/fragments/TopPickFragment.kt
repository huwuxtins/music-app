package com.example.musicapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.adapters.NewSongAdapter
import com.example.musicapp.controllers.SongController
import com.example.musicapp.models.Type
import com.google.android.material.appbar.CollapsingToolbarLayout

class TopPickFragment(private val type: Type) : Fragment(R.layout.fragment_toppick){
    private lateinit var rcvSong: RecyclerView
    private lateinit var ctl: CollapsingToolbarLayout
    private lateinit var img: ImageView
    private lateinit var tb: Toolbar
    private val songController = SongController()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_toppick, container, false)
        rcvSong = view.findViewById(R.id.rcv_songs)
        ctl = view.findViewById(R.id.ctl)
        img = view.findViewById(R.id.img)
        tb = view.findViewById(R.id.tb)

        ctl.title = type.name
        img.setBackgroundColor(Color.parseColor(type.color))
        tb.setBackgroundColor(Color.parseColor(type.color))

        songController.getSongByType(type.name, onComplete = {
            rcvSong.adapter = NewSongAdapter(view.context, it, false, null)
            rcvSong.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        })
        return view
    }
}