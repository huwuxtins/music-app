package com.example.musicapp.controllers

import android.util.Log
import com.example.musicapp.models.Album
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Song
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class AlbumController {

    private val db = Firebase.firestore

    fun getAllAlbum(onComplete: (ArrayList<Album>) -> Unit){
        val albums = ArrayList<Album>()

        db.collection("Albums")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val album = document.toObject(Album::class.java)
                    Log.e("MyApp", "Size of song: ${album.songs.size}")
                    if(album.listSong?.size!! > 0){
                        for(songReference in album.listSong!!){
                            songReference.get()
                                .addOnSuccessListener { songDocument ->
                                    val song  = songDocument.toObject(Song::class.java)
                                    if (song != null) {
                                        song.isInPlaylist = true

                                        val firestore = FirebaseFirestore.getInstance()
                                        val docRef : DocumentReference = firestore.document(song.artist)

                                        docRef.addSnapshotListener { value, error ->
                                            if (value!=null){
                                                val artist = value.toObject(Artist::class.java)
                                                song.artistName = artist?.name.toString()
                                                album.artistName = artist?.name.toString()
                                                album.artistImage = artist?.avatar.toString()
                                                Log.e("MyApp", "Song's name: ${song.name}")
                                                album.songs.add(song)
                                                albums.add(album)
                                                onComplete(albums)
                                            }else{
                                                throw Error(error?.message ?: error.toString())
                                            }
                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("MyApp", "Can't load sons of playlist, error: $exception")
                                }
                        }
                    }
                    albums.add(album)
                }
                onComplete(albums)
            }
            .addOnFailureListener { exception ->
                Log.e("MyTag", "Can't load all playlist with $exception")
            }
    }

}