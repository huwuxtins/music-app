package com.example.musicapp.controllers

import android.util.Log
import com.example.musicapp.models.Song
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class SongController {
    private val db = Firebase.firestore;

    interface ISongController{
        fun onSetAdapter()
    }

    fun getAllSong(songController: ISongController): ArrayList<Song>{
        val songs : ArrayList<Song> = ArrayList()
        db.collection("Songs").limit(6)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val song = document.toObject(Song::class.java)
                    songs.add(song)
                }

                songController.onSetAdapter()
            }
            .addOnFailureListener { exception ->
                Log.e("MyTag", "Can't load song, Error: $exception")
            }

        return songs
    }
}