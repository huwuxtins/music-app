package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Song
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class DetailSongFragment(private val song: Song): Fragment(R.layout.fragment_detail_song)  {
    private lateinit var imgSong: ImageView
    private lateinit var tvNameSong: TextView
    private lateinit var tvNameArtists: TextView
    private lateinit var tvType: TextView
    private lateinit var tvMusician: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_song, container, false)
        imgSong = view.findViewById(R.id.imgSong)
        tvNameSong = view.findViewById(R.id.tvNameSong)
        tvNameArtists = view.findViewById(R.id.tvNameArtists)
        tvType = view.findViewById(R.id.tv_type)
        tvMusician = view.findViewById(R.id.tv_musician)

        tvNameSong.text = song.name
        tvMusician.text = song.artistName

        val firestore = FirebaseFirestore.getInstance()
        val docRef : DocumentReference = firestore.document(song.artist)

        docRef.addSnapshotListener { value, error ->
            if (value!=null){
                val artist = value.toObject(Artist::class.java)
                tvNameArtists.text = artist?.name
            }else{
                throw Error(error?.message ?: error.toString())
            }
        }

        tvType.text = song.type

        if(!song.isDownloaded){
            Picasso.get().load(song.image).into(imgSong)
        }
        return view
    }
}