package com.example.musicapp.models

import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

class Playlist (
    var name: String,
    var userId: String,
    var createAt: String,
    var image: String,
    var songIds: ArrayList<DocumentReference> = ArrayList(),
    var songs: ArrayList<Song> = ArrayList()
){
    constructor() : this("", "", "", "")

    fun toMap(): Map<Serializable, Any> {

        return hashMapOf(
            "name" to this.name,
            "userId" to this.userId,
            "createAt" to this.createAt,
            "songIds" to this.songIds.map { it.path }
        )
    }
}