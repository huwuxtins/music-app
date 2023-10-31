package com.example.musicapp.models

import java.util.Date

class Song (
    var id: String,
    var name: String,
    var lyric: String,
    var link: String,
    var postAt: Date,
    var artists: ArrayList<Artist>
){
    fun getArtists(): String {
        if(artists.size > 2){
            return artists[0].name + " " + artists[1].name + " and others"
        }
        return artists[0].name + " " + artists[1].name
    }
}