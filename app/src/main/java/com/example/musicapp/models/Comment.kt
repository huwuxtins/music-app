package com.example.musicapp.models

import com.google.firebase.firestore.DocumentReference

class Comment (
    var id : String,
    var user: DocumentReference ? = null,
    var createAt: String,
    var content: String
){
    constructor() : this("",null,"","")
}