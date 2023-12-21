package com.example.musicapp.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class Song  (
    var id: Long,
    var name: String,
    var type: String,
    var lyric: String,
    var link: String,
    var postAt: String,
    var image: String,
    var artist: String,
    var artistName: String,
    var isLoved: Boolean = false,
    var isDownloaded: Boolean = false,
    var isInPlaylist: Boolean = false,
    var comments: ArrayList<String>? = null
): Serializable{

    constructor():this(0, "","", "" , "","", "", "","", false, false, false, null)

    fun getCommentsUser(): ArrayList<DocumentReference>? {

       val arr  = ArrayList<DocumentReference>()

        if(comments!=null){
            for(path in this.comments!!){
                val docRef = FirebaseFirestore.getInstance().document(path)
                arr.add(docRef)
            }
        }

        return arr
    }
}