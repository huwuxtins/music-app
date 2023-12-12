package com.example.musicapp.controllers

import android.util.Log
import com.example.musicapp.models.Playlist
import com.example.musicapp.models.Song
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class PlaylistController {
    private val db = Firebase.firestore

    fun getPlaylistByName(nameOfPlaylist: String, onComplete: (ArrayList<Playlist>) -> Unit){
        val playlists = ArrayList<Playlist>()

        db.collection("Playlists")
            .whereEqualTo("name", nameOfPlaylist)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val playlist = document.toObject(Playlist::class.java)
                    Log.e("MyApp", "Size of song: ${playlist.songIds.size}")
                    if(playlist.songIds.size > 0){
                        for(songReference in playlist.songIds){
                            songReference.get()
                                .addOnSuccessListener { songDocument ->
                                    val song  = songDocument.toObject(Song::class.java)
                                    if (song != null) {
                                        playlist.songs.add(song)
                                    }
                                    onComplete(playlists)
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("MyApp", "Can't load sons of playlist, error: $exception")
                                }
                        }
                    }
                    playlists.add(playlist)
                }
                onComplete(playlists)
            }
            .addOnFailureListener { exception ->
                Log.e("MyTag", "Can't load playlist by $nameOfPlaylist with $exception")
            }
    }

    fun addPlaylist(playlist: Playlist, userId: String, onComplete: () -> Unit){
        db.collection("Playlists")
            .document(playlist.id)
            .set(playlist.toMap())
    }
}