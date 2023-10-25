package com.example.musicapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.musicapp.R

class Login : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var txt_login : TextView ?= null
        var btn_signup : Button = findViewById(R.id.btn_signup)


        txt_login = findViewById(R.id.txt_login)
        txt_login.setOnClickListener({
            val intent = Intent(this, EnterLogin::class.java)
            startActivity(intent)
        })

        btn_signup.setOnClickListener({
            var intent = Intent(this, RegisterEmail::class.java)
            startActivity(intent)
        })




    }
}