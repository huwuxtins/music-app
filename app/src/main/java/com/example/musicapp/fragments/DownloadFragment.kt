package com.example.musicapp.fragments

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.adapters.NewSongAdapter
import com.example.musicapp.adapters.PlaylistAdapter
import com.example.musicapp.models.Playlist
import com.example.musicapp.models.Song
import java.io.File

class DownloadFragment: Fragment() {
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

        val songs1 = getDownloadedSong()

        val playlists = getDownloadedPlaylists()

        val playlistAdapter = PlaylistAdapter(view.context, playlists)
        rcvPlaylist.adapter = playlistAdapter
        rcvPlaylist.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        val songAdapter = NewSongAdapter(view.context, songs1, false, null)
        rcvSong.adapter = songAdapter
        rcvSong.hasFixedSize()
        rcvSong.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        return view
    }

    private fun getDownloadedSong(): ArrayList<Song>{
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/MusicApp")

        val downloadedPlaylist: ArrayList<Song> = ArrayList()
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        if(sharedPreferences != null){
            if(directory.isDirectory){
                if(directory.listFiles() != null){
                    for(file in directory.listFiles()!!){
                        val song = Song()
                        val songId = file.name.split(".")[0]
                        val songString = sharedPreferences.getString("downloaded_song_${songId}", "")
                        if(songString != ""){
                            val splitSongString = songString?.split("_")
                            if (splitSongString != null) {
                                song.id = splitSongString[0].toLong()
                                song.name = splitSongString[1]
                                song.type = splitSongString[2]
                                song.lyric = splitSongString[3]
                                song.postAt = splitSongString[4]
                                song.artist = splitSongString[5]
                                song.artistName = splitSongString[6]
                                song.isLoved = splitSongString[7].toBoolean()
                                song.link = file.path
                                song.isDownloaded = true
                                downloadedPlaylist.add(song)
                            }
                        }
                    }
                }
            }
        }

        return downloadedPlaylist
    }

    private fun getDownloadedPlaylists(): ArrayList<Playlist> {
        val musicAppDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "/MusicApp"
        )

        val downloadedPlaylists: ArrayList<Playlist> = ArrayList()
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        if (sharedPreferences != null) {
            if (musicAppDirectory.isDirectory) {
                val playlistFolders = musicAppDirectory.listFiles()

                if (playlistFolders != null) {
                    for (playlistFolder in playlistFolders) {
                        if (playlistFolder.isDirectory) {
                            val playlist = Playlist()
                            playlist.name = playlistFolder.name

                            val downloadedSongs: ArrayList<Song> = ArrayList()
                            val songFiles = playlistFolder.listFiles()

                            if (songFiles != null) {
                                for (file in songFiles) {
                                    val song = Song()
                                    val songId = file.name.split(".")[0]
                                    val songString =
                                        sharedPreferences.getString("downloaded_song_${songId}", "")
                                    if (songString != "") {
                                        val splitSongString = songString?.split("_")
                                        if (splitSongString != null && splitSongString.size >= 8) {
                                            song.id = splitSongString[0].toLong()
                                            song.name = splitSongString[1]
                                            song.type = splitSongString[2]
                                            song.lyric = splitSongString[3]
                                            song.postAt = splitSongString[4]
                                            song.artist = splitSongString[5]
                                            song.artistName = splitSongString[6]
                                            song.isLoved = splitSongString[7].toBoolean()
                                            song.link = file.path
                                            song.isDownloaded = true
                                            downloadedSongs.add(song)
                                        }
                                    }
                                }
                            }

                            playlist.songs = downloadedSongs
                            downloadedPlaylists.add(playlist)
                        }
                    }
                }
            }
        }
        return downloadedPlaylists
    }

}