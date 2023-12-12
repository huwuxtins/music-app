package com.example.musicapp.models

import java.io.Serializable
import java.util.Date

class Song (
    var id: String,
    var name: String,
    var lyric: String,
    var link: String,
    var postAt: Date,
    var image: String,
    var artists: ArrayList<Artist>,
): Serializable{
    fun getArtists(): String {
        if(artists.size > 2){
            return artists[0].name + " " + artists[1].name + " and others"
        }
        return artists[0].name + " " + artists[1].name
    }

    override fun toString(): String {
        return "Song(id='$id', name='$name', lyric='$lyric', link='$link', postAt=$postAt, artists=$artists)"
    }
}