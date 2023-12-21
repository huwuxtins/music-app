package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.adapters.ArtistsFavouriteAdapter
import com.example.musicapp.models.Artist
import com.example.musicapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class ListMyArtistFragment : Fragment() {

    private lateinit var img_back : ImageView
    private lateinit var list : RecyclerView
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var listArtist : ArrayList<DocumentReference>
    private lateinit var adapter : ArtistsFavouriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_my_artist, container, false)
        img_back = view.findViewById(R.id.img_back)
        list = view.findViewById(R.id.list)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        listArtist  = ArrayList()

        adapter = activity?.let { ArtistsFavouriteAdapter(view.context, listArtist) }!!
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        list.adapter = adapter
        list!!.layoutManager = layoutManager
        list.addItemDecoration(
            DividerItemDecoration(
                context,
                layoutManager.orientation
            )
        )
        showListArtists()

        img_back.setOnClickListener{
            onBackPressed()
        }

        return view
    }

    fun onBackPressed() {
        if (parentFragmentManager.backStackEntryCount > 0) {
            parentFragmentManager.popBackStack()
        }
    }


    fun showListArtists(){
        val fUser = auth.currentUser
        val email = fUser?.email.toString()
        db.collection("Users").document(email).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                val l = user?.getListArtist()
                if (l != null) {
                    adapter.setData(l)
                    adapter.notifyDataSetChanged()
                }

            }

    }

}