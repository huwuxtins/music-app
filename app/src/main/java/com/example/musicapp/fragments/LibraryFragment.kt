package com.example.musicapp.fragments

import android.icu.lang.UCharacter.VerticalOrientation
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.adapters.ArtistAdapter
import com.example.musicapp.adapters.NewSongAdapter
import com.example.musicapp.adapters.PlaylistAdapter
import com.example.musicapp.controllers.PlaylistController
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Playlist
import com.example.musicapp.models.Song
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class LibraryFragment() : Fragment(R.layout.fragment_library) {
    private lateinit var rcvPlaylist: RecyclerView
    private lateinit var rcvArtist: RecyclerView
    private lateinit var rcvSong: RecyclerView
    private lateinit var cstFavSong: ConstraintLayout
    private lateinit var cstDownSong: ConstraintLayout

    private val playlistController: PlaylistController = PlaylistController()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        rcvPlaylist = view.findViewById(R.id.rcvPlaylist)
        rcvArtist = view.findViewById(R.id.rcvArtist)
        rcvSong = view.findViewById(R.id.rcvHistory)
        cstFavSong = view.findViewById(R.id.cstFavSong)
        cstDownSong = view.findViewById(R.id.cstDownSong)

        val mainActivity = context as MainActivity

        val artists = ArrayList<Artist>()

        val songs1 = ArrayList<Song>()

        val songs2 = ArrayList<Song>()

        val playlists = ArrayList<Playlist>()

        cstFavSong.setOnClickListener{
            try {
                playlistController.getPlaylistByName("Lovely", onComplete = {

                    mainActivity.loadFragment(PlaylistFragment(it[0]), "body")
                })
            }
            catch (e : Exception){
                Toast.makeText(context, "Don't have any songs in here", Toast.LENGTH_LONG).show();
            }
        }
        cstDownSong.setOnClickListener{
            mainActivity.loadFragment(DownloadFragment(), "body")
        }

        playlistController.getPlaylistByName("Lovely", onComplete = {
            val playlistAdapter = PlaylistAdapter(view.context, it)
            rcvPlaylist.adapter = playlistAdapter
            rcvPlaylist.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        })

        val artistAdapter = ArtistAdapter(view.context, artists)
        rcvArtist.adapter = artistAdapter
        rcvArtist.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        val songAdapter = NewSongAdapter(view.context, songs1)
        rcvSong.adapter = songAdapter
        rcvSong.hasFixedSize()
        rcvSong.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        return view
    }


}