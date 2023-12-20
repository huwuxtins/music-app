package com.example.musicapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.adapters.NewSongAdapter
import com.example.musicapp.adapters.SingerAdapter
import com.example.musicapp.adapters.TypeAdapter
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Song
import com.example.musicapp.models.Type
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var listType : ArrayList<Type>
    private lateinit var recyclerView : RecyclerView
    private lateinit var typeAdapter : TypeAdapter
    private lateinit var recyclerViewSinger : RecyclerView
    private lateinit var singerAdapter: SingerAdapter
    private lateinit var img_account : ImageView
    private lateinit var listSinger : ArrayList<Artist>

    private lateinit var recyclerviewNewSong : RecyclerView
    private lateinit var listNewSong : ArrayList<Song>
    private lateinit var newsongAdapter: NewSongAdapter

    private lateinit var txt_singer : TextView

    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        listType = createList()
        recyclerView =  view.findViewById(R.id.listKind)
        recyclerViewSinger = view.findViewById(R.id.recyclerView)
        img_account = view.findViewById(R.id.img_account)
        db = FirebaseFirestore.getInstance()

        typeAdapter = activity?.let { TypeAdapter(view.context,listType) }!!
        recyclerView.adapter = typeAdapter
        recyclerView!!.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        listSinger = ArrayList<Artist>()
        singerAdapter = activity?.let { SingerAdapter(view.context,listSinger) }!!
        recyclerViewSinger.adapter = singerAdapter
        recyclerViewSinger!!.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        listSinger = getListSinger()

        recyclerviewNewSong = view.findViewById(R.id.listNewSong)
        listNewSong = ArrayList<Song>()
        newsongAdapter = activity?.let { NewSongAdapter(view.context, listNewSong, false) }!!
        recyclerviewNewSong.adapter = newsongAdapter
        recyclerviewNewSong!!.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        listNewSong = getListNewSong()

        txt_singer = view.findViewById(R.id.txt_singer)

        txt_singer.setOnClickListener { goToArtistFragment(); }

        img_account.setOnClickListener {
            val mainActivity = context as MainActivity
            mainActivity.loadFragment(InfoFragment(), "body")
        }

        return view
    }

    private fun createList() : ArrayList<Type>{
        val l : ArrayList<Type> = ArrayList<Type>()
        l.add(Type("Ballad","#0075FF"))
        l.add(Type("POP","#FF00E5"))
        l.add(Type("EDM","#35BB52"))
        l.add(Type("K-POP","#E65880"))
        l.add(Type("US-UK","#1FB6BA"))
        l.add(Type("C-POP","#20DB8A"))
        return l
    }

    private fun getListSinger() : ArrayList<Artist>{  //lay list artist tu database => set vao adapter
        val l : ArrayList<Artist> = ArrayList<Artist>()
        db.collection("Artists").limit(6)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val art = document.toObject(Artist::class.java)
                    l.add(art)
                }
                singerAdapter.setData(l)
                singerAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {

            }
        return l
    }

    private  fun getListNewSong(): ArrayList<Song>{
        val l : ArrayList<Song> = ArrayList<Song>()
        db.collection("Songs").orderBy("id", Query.Direction.DESCENDING).limit(4)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val s = document.toObject(Song::class.java)

                    val firestore = FirebaseFirestore.getInstance()
                    val docRef : DocumentReference = firestore.document(s.artist)

                    docRef.addSnapshotListener { value, error ->
                        if (value!=null){
                            val artist = value.toObject(Artist::class.java)
                            s.artistName = artist?.name.toString()
                            l.add(s)
                            newsongAdapter.setData(l)
                            newsongAdapter.notifyDataSetChanged()
                        }else{
                            throw Error(error?.message ?: error.toString())
                        }
                    }
                }
            }
            .addOnFailureListener {
            }

        return l
    }

    private fun goToArtistFragment(){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frgMain, ArtistsFragment())
        transaction?.addToBackStack("null")
        transaction?.commit()
    }

}