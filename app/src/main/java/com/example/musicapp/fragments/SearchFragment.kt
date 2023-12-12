package com.example.musicapp.fragments

import android.app.SearchManager
import android.content.Intent
import android.content.SearchRecentSuggestionsProvider
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.adapters.AlbumAdapter
import com.example.musicapp.adapters.ArtistsSearchAdapter
import com.example.musicapp.adapters.NewSongAdapter
import com.example.musicapp.adapters.SingerAdapter
import com.example.musicapp.models.Album
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Song
import com.example.musicapp.others.MySuggestionProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject


class SearchFragment(): Fragment(R.layout.fragment_search) {

    lateinit var searchView : SearchView
    lateinit var searchSong : Button
    lateinit var searchArtist : Button
    lateinit var searchAlbum : Button
    lateinit var recyclerView: RecyclerView
    lateinit var listSong : ArrayList<Song>
    lateinit var listArtist : ArrayList<Artist>
    lateinit var listAlbum : ArrayList<Album>
    lateinit var singerAdapter: ArtistsSearchAdapter
    lateinit var songAdapter: NewSongAdapter
    lateinit var albumAdapter : AlbumAdapter
    lateinit var key : String
    lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        searchView = view.findViewById(R.id.edt_search)

        db = FirebaseFirestore.getInstance()

        searchSong = view.findViewById(R.id.searchSong)
        searchAlbum = view.findViewById(R.id.searchAlbum)
        searchArtist = view.findViewById(R.id.searchArtist)
        recyclerView = view.findViewById(R.id.listResult)

        searchSong.visibility = View.GONE
        searchAlbum.visibility = View.GONE
        searchArtist.visibility = View.GONE

        listSong = ArrayList()
        listAlbum = ArrayList()
        listArtist = ArrayList()

        songAdapter = activity?.let { NewSongAdapter(it.applicationContext,listSong) }!!
        singerAdapter = activity?.let { ArtistsSearchAdapter(it.applicationContext,listArtist) }!!
        albumAdapter = activity?.let { AlbumAdapter(it.applicationContext,listAlbum) }!!

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                //Toast.makeText(activity,query,Toast.LENGTH_SHORT).show()
                key = query
                searchSong.visibility = View.VISIBLE
                searchAlbum.visibility = View.VISIBLE
                searchArtist.visibility = View.VISIBLE
                searchSong.performClick()
                return false
            }
        })

        searchSong.setOnClickListener{
            setColor(searchSong,searchArtist,searchAlbum)
        //    Toast.makeText(activity,key,Toast.LENGTH_SHORT).show()
            if(listSong.size != 0){
                listSong.clear()
            }
            db.collection("Songs").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val song = document.toObject(Song::class.java)
                        if(song.name.contains(key)){
                            listSong.add(song)
                        }
                    }
                    songAdapter.setData(listSong)
                    recyclerView.adapter = songAdapter
                    recyclerView!!.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
                    songAdapter.notifyDataSetChanged()
                }

                .addOnFailureListener{ exception ->
                    Toast.makeText(activity,"Error system",Toast.LENGTH_SHORT).show()
                }
        }

        searchArtist.setOnClickListener{
            setColor(searchArtist,searchSong,searchAlbum)
            if(listArtist.size != 0){
                listArtist.clear()
            }
            db.collection("Artists").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val art = document.toObject(Artist::class.java)
                        if(art.name.contains(key)){
                            listArtist.add(art)
                        }
                    }
                    singerAdapter.setData(listArtist)
                    recyclerView.adapter = singerAdapter
                    recyclerView!!.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
                    singerAdapter.notifyDataSetChanged()
                }

                .addOnFailureListener{ exception ->
                    Toast.makeText(activity,"Error system",Toast.LENGTH_SHORT).show()
                }

        }


        searchAlbum.setOnClickListener{
            setColor(searchAlbum,searchSong,searchArtist)
            if(listAlbum.size != 0){
                listAlbum.clear()
            }

            db.collection("Albums").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val album = document.toObject(Album::class.java)
                        if(album.name.contains(key)){
                            listAlbum.add(album)
                        }
                    }
                    albumAdapter.setData(listAlbum)
                    recyclerView.adapter = albumAdapter
                    recyclerView!!.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
                    albumAdapter.notifyDataSetChanged()
                }

                .addOnFailureListener{ exception ->
                    Toast.makeText(activity,"Error system",Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }



    private fun setColor(btn1 : Button, btn2 : Button, btn3: Button){
        btn1.setBackgroundColor(resources.getColor(R.color.selected))
        btn2.setBackgroundColor(resources.getColor(R.color.black))
        btn3.setBackgroundColor(resources.getColor(R.color.black))
    }

}