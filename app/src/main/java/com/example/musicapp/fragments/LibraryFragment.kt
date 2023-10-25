package com.example.musicapp.fragments

import android.icu.lang.UCharacter.VerticalOrientation
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.adapters.PlaylistAdapter
import com.example.musicapp.models.Playlist
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class LibraryFragment() : Fragment(R.layout.fragment_library) {
    private lateinit var rcvPlaylist: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        rcvPlaylist = view.findViewById(R.id.rcvPlayList)
        val playlists = ArrayList<Playlist>()
        playlists.add(
            Playlist(
                "id1",
                "playlist1",
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
            )
        )

        val playlistAdapter = PlaylistAdapter(view.context, playlists)
        rcvPlaylist.adapter = playlistAdapter
        rcvPlaylist.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        return view
    }


}