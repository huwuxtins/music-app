package com.example.musicapp.controllers

import android.util.Log
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Song
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class SongController {
    private val db = Firebase.firestore;

    fun getAllSong(onComplete: () -> Unit): ArrayList<Song>{
        val songs : ArrayList<Song> = ArrayList()
        db.collection("Songs").limit(6)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val song = document.toObject(Song::class.java)

                    val firestore = FirebaseFirestore.getInstance()
                    val docRef : DocumentReference = firestore.document(song.artist)

                    docRef.addSnapshotListener { value, error ->
                        if (value!=null){
                            val artist = value.toObject(Artist::class.java)
                            song.artistName = artist?.name.toString()
                        }else{
                            throw Error(error?.message ?: error.toString())
                        }
                    }
                    songs.add(song)
                }

                onComplete()
            }
            .addOnFailureListener { exception ->
                Log.e("MyTag", "Can't load song, Error: $exception")
            }

        return songs
    }

    fun getSongByType(type: String, onComplete: (ArrayList<Song>) -> Unit): ArrayList<Song>{
        val songs : ArrayList<Song> = ArrayList()
        db.collection("Songs").limit(6)
            .whereEqualTo("type", type)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val song = document.toObject(Song::class.java)

                    val firestore = FirebaseFirestore.getInstance()
                    val docRef : DocumentReference = firestore.document(song.artist)

                    docRef.addSnapshotListener { value, error ->
                        if (value!=null){
                            val artist = value.toObject(Artist::class.java)
                            Log.e("MyApp", "Song's name: ${song.name}")
                            song.artistName = artist?.name.toString()
                            songs.add(song)

                            onComplete(songs)
                        }else{
                            throw Error(error?.message ?: error.toString())
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MyTag", "Can't load song, Error: $exception")
            }

        return songs
    }
}