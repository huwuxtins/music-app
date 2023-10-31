package com.example.musicapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.musicapp.R

class RegisterPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_password)

        var img_backEmail : ImageView = findViewById(R.id.img_backEmail)

        img_backEmail.setOnClickListener({
            onBackPressed()
        })
    }
}