package com.example.musicapp.models

import java.io.Serializable
import java.util.Date

class Artist (
    var id: Long,
    var name: String,
    var bod: String,
    var nationality: String,
    var description: String,
    var avatar: String,
    var followers: Int,
    var realName: String
) : Serializable {
    constructor() :    this(0, "", "" , "","","",0,"")
}