package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.adapters.NewSongAdapter
import com.example.musicapp.adapters.SingerAdapter
import com.example.musicapp.adapters.TypeAdapter
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Song
import com.example.musicapp.models.Type
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

class HomeFragment(): Fragment(R.layout.fragment_home) {

    lateinit var listType : ArrayList<Type>
    lateinit var recyclerView : RecyclerView
    lateinit var typeAdpater : TypeAdapter
    lateinit var recyclerViewSinger : RecyclerView
    lateinit var singerAdapter: SingerAdapter
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
             db = FirebaseFirestore.getInstance()

            typeAdpater = activity?.let { TypeAdapter(it.applicationContext,listType) }!!
            recyclerView.adapter = typeAdpater
            recyclerView!!.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.HORIZONTAL, false)

            listSinger = ArrayList<Artist>()
            singerAdapter = activity?.let { SingerAdapter(it.applicationContext,listSinger) }!!
            recyclerViewSinger.adapter = singerAdapter
            recyclerViewSinger!!.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.HORIZONTAL, false)
            listSinger = getListSinger()


            recyclerviewNewSong = view.findViewById(R.id.listNewSong)
            listNewSong = ArrayList<Song>()
            newsongAdapter = activity?.let { NewSongAdapter(it.applicationContext,listNewSong) }!!
            recyclerviewNewSong.adapter = newsongAdapter
            recyclerviewNewSong!!.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.VERTICAL, false)
            listNewSong = getListNewSong()

            txt_singer = view.findViewById(R.id.txt_singer)

            txt_singer.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                        goToArtistFragment();
                }

            })

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
            .addOnFailureListener { exception ->

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
                    l.add(s)
                }
                newsongAdapter.setData(l)
                newsongAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
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