package com.example.musicapp.models

import java.util.Date

class Playlist (
    var id: String,
    var name: String,
//    var user: User,
    var createAt: Date,
    var songs: ArrayList<Song>
)