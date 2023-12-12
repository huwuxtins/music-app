package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.adapters.NewSongAdapter
import com.example.musicapp.adapters.PlaylistAdapter
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Playlist
import com.example.musicapp.models.Song
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class DownloadFragment(): Fragment() {
    private lateinit var rcvPlaylist: RecyclerView
    private lateinit var rcvSong: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_download, container, false)
        rcvPlaylist = view.findViewById(R.id.rcvPlaylist)
        rcvSong = view.findViewById(R.id.rcvSong)

        val artists = ArrayList<Artist>()

        val songs1 = ArrayList<Song>()

        val playlists = ArrayList<Playlist>()

        val playlistAdapter = PlaylistAdapter(view.context, playlists)
        rcvPlaylist.adapter = playlistAdapter
        rcvPlaylist.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        val songAdapter = NewSongAdapter(view.context, songs1)
        rcvSong.adapter = songAdapter
        rcvSong.hasFixedSize()
        rcvSong.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        return view
    }
}