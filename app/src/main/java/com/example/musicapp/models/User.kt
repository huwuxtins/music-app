package com.example.musicapp.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class User(
    var username: String,
    var email: String,
    var password: String,
    var gender: String,
    var status: Boolean,
    var avatar: String,
    var uid: String,
    var type: String,
    var favouriteArtist : ArrayList<String> ?= null

){
    constructor() :    this("", "", "", "",true,"","","",null)

    fun toMap() : HashMap<String, Any>{
        val user : HashMap<String,Any> = HashMap<String, Any>()
        user.put("username", username);
        user.put("password", password);
        user.put("email",email)
        user.put("gender",gender)
        user.put("status",status)
        user.put("avatar",avatar)
        user.put("uid",uid)
        user.put("type",type)
        return user;
    }

    fun getListArtist(): ArrayList<DocumentReference>{
        val arr  = ArrayList<DocumentReference>()

        if(favouriteArtist!=null){
            for(path in this.favouriteArtist!!){
                val docRef = FirebaseFirestore.getInstance().document(path)
                arr.add(docRef)
            }
        }
        return arr
    }

}