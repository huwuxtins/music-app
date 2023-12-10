package com.example.musicapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.adapters.ArtistAdapter
import com.example.musicapp.adapters.SingerAdapter
import com.example.musicapp.models.Artist
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.firestore.FirebaseFirestore


class ArtistsFragment : Fragment() {
    lateinit var searchView : SearchView
    lateinit var recyclerView: RecyclerView
    lateinit var list : ArrayList<Artist>
    lateinit var artistAdapter: SingerAdapter
    lateinit var db : FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_artists, container, false)
        searchView = view.findViewById(R.id.edt_search22)

        recyclerView = view.findViewById(R.id.listArtists)

        db = FirebaseFirestore.getInstance()

        list = ArrayList()

        artistAdapter = activity?.let { SingerAdapter(it.applicationContext,list) }!!
        recyclerView.adapter = artistAdapter

        val layoutManager = FlexboxLayoutManager(requireActivity().applicationContext)
        layoutManager.flexDirection = FlexDirection.ROW;
        layoutManager.justifyContent = JustifyContent.FLEX_START;

//        recyclerView!!.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.layoutManager = layoutManager
        list = getListSinger()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(activity,query, Toast.LENGTH_SHORT).show()
                return false
            }
        })
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getListSinger() : ArrayList<Artist>{  //lay list artist tu database => set vao adapter
        val l : ArrayList<Artist> = ArrayList<Artist>()
        db.collection("Artists")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val art = document.toObject(Artist::class.java)
                    l.add(art)
                }
                artistAdapter.setData(l)
                artistAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->

            }
        return l
    }
}