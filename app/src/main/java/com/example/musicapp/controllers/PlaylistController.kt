package com.example.musicapp.controllers

import android.util.Log
import android.widget.Toast
import com.example.musicapp.models.Playlist
import com.example.musicapp.models.Song
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class PlaylistController {
    private val db = Firebase.firestore

    fun getAllPlaylist(onComplete: (ArrayList<Playlist>) -> Unit){
        val playlists = ArrayList<Playlist>()

        db.collection("Playlists")
            .whereNotEqualTo("name", "Lovely")
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
                Log.e("MyTag", "Can't load all playlist with $exception")
            }
    }

    fun getPlaylistByName(nameOfPlaylist: String, onComplete: (Playlist) -> Unit){
        db.collection("Playlists")
            .document(nameOfPlaylist)
            .get()
            .addOnSuccessListener { result ->
                val playlist = result.toObject(Playlist::class.java)
                if(playlist != null){
                    if(playlist.songIds.size > 0){
                        for(songReference in playlist.songIds){
                            songReference.get()
                                .addOnSuccessListener { songDocument ->
                                    val song  = songDocument.toObject(Song::class.java)
                                    if (song != null) {
                                        if(nameOfPlaylist == "Lovely"){
                                            song.isLoved = true
                                        }
                                        playlist.songs.add(song)
                                    }
                                    onComplete(playlist)
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("MyApp", "Can't load sons of playlist, error: $exception")
                                }
                        }
                    }
                    onComplete(playlist)
                }
            }
    }

    fun addPlaylist(playlist: Playlist, onComplete: () -> Unit, onFail: () -> Unit){
        db.collection("Playlists")
            .document(playlist.name)
            .set(playlist.toMap())
            .addOnSuccessListener {
                onComplete()
            }
            .addOnFailureListener {
                onFail()
            }
    }

    fun updatePlaylist(action: String, song: Song, nameOfPlaylist: String, onComplete: () -> Unit, onFail: () -> Unit){
        val playlist = db.collection("Playlists")
            .document(nameOfPlaylist)
        val songReference = db.collection("Songs").document(song.id.toString())
        when(action){
            "add" -> {
                playlist.update("songIds", FieldValue.arrayUnion(songReference))
                    .addOnSuccessListener {
                        onComplete()
                    }
                    .addOnFailureListener {
                        onFail()
                    }
            }
            "remove" -> {
                playlist.update("songIds", FieldValue.arrayRemove(songReference))
                    .addOnSuccessListener {
                        onComplete()
                    }
                    .addOnFailureListener {
                        onFail()
                    }
            }
        }
    }
}