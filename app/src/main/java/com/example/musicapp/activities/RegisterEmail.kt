package com.example.musicapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.musicapp.R

class RegisterEmail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_email)

        var img_backStart : ImageView = findViewById(R.id.img_backStart)
        var btn_nextPass : Button = findViewById(R.id.btn_nextPass)

        img_backStart.setOnClickListener({
            onBackPressed()
        })

        btn_nextPass.setOnClickListener({
            var intent = Intent(this, RegisterPassword::class.java)
            startActivity(intent)
        })


    }
}