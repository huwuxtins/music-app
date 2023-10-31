package com.example.musicapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.musicapp.R

class EnterLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_login)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        var img_back : ImageView ?= null
        img_back = findViewById(R.id.img_back)

        img_back.setOnClickListener({
            onBackPressed();
        })


    }
}