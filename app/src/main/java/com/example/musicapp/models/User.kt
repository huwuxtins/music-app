package com.example.musicapp.models

class User (
    var username: String,
    var email: String,
    var password: String,
    var gender: String,
    var status: Boolean,
    var avatar: String
){
    fun toMap() : HashMap<String, Any>{
        val user : HashMap<String,Any> = HashMap<String, Any>()
        user.put("username", username);
        user.put("password", password);
        user.put("email",email)
        user.put("gender",gender)
        user.put("status",status)
        user.put("avatar",avatar)
        return user;
    }

}