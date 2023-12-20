package com.example.musicapp.controllers

import android.util.Log
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Playlist
import com.example.musicapp.models.Song
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class PlaylistController {
    private val db = Firebase.firestore

    fun getAllPlaylist(userId: String, onComplete: (ArrayList<Playlist>) -> Unit){
        val playlists = ArrayList<Playlist>()

        db.collection("Playlists")
            .whereEqualTo("userId", userId)
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

                                        val firestore = FirebaseFirestore.getInstance()
                                        val docRef : DocumentReference = firestore.document(song.artist)

                                        docRef.addSnapshotListener { value, error ->
                                            if (value!=null){
                                                val artist = value.toObject(Artist::class.java)
                                                song.artistName = artist?.name.toString()
                                                playlist.songs.add(song)
                                                onComplete(playlists)
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
                    playlists.add(playlist)
                }
                onComplete(playlists)
            }
            .addOnFailureListener { exception ->
                Log.e("MyTag", "Can't load all playlist with $exception")
            }
    }

    fun getPlaylistByName(userId: String, nameOfPlaylist: String, onComplete: (Playlist) -> Unit){
        db.collection("Playlists")
            .document(nameOfPlaylist+"_$userId")
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

                                        val firestore = FirebaseFirestore.getInstance()
                                        val docRef : DocumentReference = firestore.document(song.artist)

                                        docRef.addSnapshotListener { value, error ->
                                            if (value!=null){
                                                val artist = value.toObject(Artist::class.java)
                                                song.artistName = artist?.name.toString()
                                                playlist.songs.add(song)
                                                onComplete(playlist)
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
                    onComplete(playlist)
                }
            }
    }

    fun addPlaylist(playlist: Playlist, onComplete: () -> Unit, onFail: () -> Unit){
        val playlistReference = db.collection("Playlists").document(playlist.name+"_${playlist.userId}")

        playlistReference.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        onFail()
                    } else {
                        db.collection("Playlists")
                            .document(playlist.name+"_${playlist.userId}")
                            .set(playlist.toMap())
                            .addOnSuccessListener {
                                onComplete()
                            }
                            .addOnFailureListener {
                                onFail()
                            }
                    }
                } else {
                    // An error occurred while checking for the document
                    onFail()
                }
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