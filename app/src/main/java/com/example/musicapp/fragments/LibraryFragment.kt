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
import com.example.musicapp.adapters.ArtistAdapter
import com.example.musicapp.adapters.PlaylistAdapter
import com.example.musicapp.adapters.SongAdapter
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        rcvPlaylist = view.findViewById(R.id.rcvPlayList)
        rcvArtist = view.findViewById(R.id.rcvArtist)
        rcvSong = view.findViewById(R.id.rcvHistory)

        val playlists = ArrayList<Playlist>()
        playlists.add(
            Playlist(
                "id1",
                "playlist1",
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
            )
        )
        playlists.add(
            Playlist(
                "id1",
                "playlist1",
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
            )
        )
        playlists.add(
            Playlist(
                "id1",
                "playlist1",
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
            )
        )

        val artists = ArrayList<Artist>()
        artists.add(Artist(
            "id1",
            "Alan Walker",
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
            "British",
            "Alan Olav Walker, thường được biết đến với nghệ danh Alan Walker là một nam DJ và nhà sản xuất thu âm người Anh gốc Na Uy Vào năm 2015, Alan bắt đầu trở nên nổi tiếng trên phạm vi quốc tế sau khi phát hành đĩa đơn \"Faded\" và nhận được chứng nhận bạch kim tại 14 quốc gia.",
            "alan_walker",
            45000000
        ))
        artists.add(Artist(
            "id1",
            "Alan Walker",
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
            "British",
            "Alan Olav Walker, thường được biết đến với nghệ danh Alan Walker là một nam DJ và nhà sản xuất thu âm người Anh gốc Na Uy Vào năm 2015, Alan bắt đầu trở nên nổi tiếng trên phạm vi quốc tế sau khi phát hành đĩa đơn \"Faded\" và nhận được chứng nhận bạch kim tại 14 quốc gia.",
            "alan_walker",
            45000000
        ))
        artists.add(Artist(
            "id1",
            "Alan Walker",
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
            "British",
            "Alan Olav Walker, thường được biết đến với nghệ danh Alan Walker là một nam DJ và nhà sản xuất thu âm người Anh gốc Na Uy Vào năm 2015, Alan bắt đầu trở nên nổi tiếng trên phạm vi quốc tế sau khi phát hành đĩa đơn \"Faded\" và nhận được chứng nhận bạch kim tại 14 quốc gia.",
            "alan_walker",
            45000000
        ))

        val songs = ArrayList<Song>()
        songs.add(Song(
            "id1",
            "Faded",
            "Faded",
            "faded.link",
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
            artists
        ))
        songs.add(Song(
            "id1",
            "Faded",
            "Faded",
            "faded.link",
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
            artists
        ))
        songs.add(Song(
            "id1",
            "Faded",
            "Faded",
            "faded.link",
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
            artists
        ))
        songs.add(Song(
            "id1",
            "Faded",
            "Faded",
            "faded.link",
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
            artists
        ))
        songs.add(Song(
            "id1",
            "Faded",
            "Faded",
            "faded.link",
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
            artists
        ))
        songs.add(Song(
            "id1",
            "Faded",
            "Faded",
            "faded.link",
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
            artists
        ))
        songs.add(Song(
            "id1",
            "Faded",
            "Faded",
            "faded.link",
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
            artists
        ))
        songs.add(Song(
            "id1",
            "Faded",
            "Faded",
            "faded.link",
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
            artists
        ))

        val playlistAdapter = PlaylistAdapter(view.context, playlists)
        rcvPlaylist.adapter = playlistAdapter
        rcvPlaylist.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        val artistAdapter = ArtistAdapter(view.context, artists)
        rcvArtist.adapter = artistAdapter
        rcvArtist.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        val songAdapter = SongAdapter(view.context, songs)
        rcvSong.adapter = songAdapter
        rcvSong.hasFixedSize()
        rcvSong.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        return view
    }


}