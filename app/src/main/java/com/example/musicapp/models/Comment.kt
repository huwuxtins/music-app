package com.example.musicapp.models

import java.util.Date

class Comment (
    var user: User,
    var song: Song,
    var createAt: Date,
    var content: String
)