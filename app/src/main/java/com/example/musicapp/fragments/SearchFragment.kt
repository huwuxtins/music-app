package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
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
import com.example.musicapp.models.Album
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Song
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment: Fragment(R.layout.fragment_search) {

    private lateinit var searchView : SearchView
    lateinit var searchSong : Button
    lateinit var searchArtist : Button
    lateinit var searchAlbum : Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var listSong : ArrayList<Song>
    private lateinit var listArtist : ArrayList<Artist>
    private lateinit var listAlbum : ArrayList<Album>
    private lateinit var singerAdapter: ArtistsSearchAdapter
    private lateinit var songAdapter: NewSongAdapter
    private lateinit var albumAdapter : AlbumAdapter
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

        songAdapter = activity?.let { NewSongAdapter(view.context,listSong, false, null) }!!
        singerAdapter = activity?.let { ArtistsSearchAdapter(view.context,listArtist) }!!
        albumAdapter = activity?.let { AlbumAdapter(view.context,listAlbum) }!!

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

                        val firestore = FirebaseFirestore.getInstance()
                        val docRef : DocumentReference = firestore.document(song.artist)

                        docRef.addSnapshotListener { value, error ->
                            if (value!=null){
                                val artist = value.toObject(Artist::class.java)
                                song.artistName = artist?.name.toString()

                                if(song.name.contains(key)){
                                    listSong.add(song)
                                }
                                songAdapter.setData(listSong)
                                recyclerView.adapter = songAdapter
                                recyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
                                songAdapter.notifyDataSetChanged()
                            }else{
                                throw Error(error?.message ?: error.toString())
                            }
                        }
                    }
                }

                .addOnFailureListener{
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
                    recyclerView!!.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
                    singerAdapter.notifyDataSetChanged()
                }

                .addOnFailureListener{
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
                    recyclerView!!.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
                    albumAdapter.notifyDataSetChanged()
                }

                .addOnFailureListener{
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