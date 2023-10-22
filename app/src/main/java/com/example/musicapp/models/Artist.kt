package com.example.musicapp.models

import java.util.Date

class Artist (
    var id: String,
    var name: String,
    var bod: Date,
    var nationality: String,
    var description: String,
    var avatar: String,
    var followers: Int
)