package com.example.musicapp.models

import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

class Playlist (
    var id: String,
    var name: String,
    var userId: String,
    var createAt: String,
    var image: String,
    var songIds: ArrayList<DocumentReference> = ArrayList(),
    var songs: ArrayList<Song> = ArrayList()
){
    constructor() : this("", "", "", "", "")

    fun toMap(): Map<Serializable, Any> {

        return hashMapOf(
            id to this.id,
            name to this.name,
            userId to this.userId,
            createAt to this.createAt,
            songIds to this.songIds
        )
    }
}