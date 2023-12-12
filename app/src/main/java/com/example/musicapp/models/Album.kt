package com.example.musicapp.models

import com.google.firebase.firestore.DocumentReference

class Album(

    var id: Long,
    var name: String,
    var postYear: String,
    var image: String,
    var artist: DocumentReference? = null,
    var listSong: ArrayList<DocumentReference>?
)
{
    constructor() :    this(0, "", "" , "",null, null )

}