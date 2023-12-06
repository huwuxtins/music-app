package com.example.musicapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.musicapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tomer.fadingtextview.FadingTextView

class splash : AppCompatActivity() {

    lateinit var fading : FadingTextView

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        var arr2 = arrayOf<String>("MUSIC APP", "Don’t just hear the music – feel it!" ,"v1.0")

        fading = findViewById(R.id.fadingTextView)

        fading.setTexts(arr2)

        val handler  = Handler()
        handler.postDelayed(Runnable {
           checkLogin()
        },4000)

    }


    fun checkLogin(){
        val user : FirebaseUser? = auth.currentUser

        if(user == null){ //go to Login
            goToLogin()
        }
        else{ //go to main
            goToMain()
        }

        finish()
    }

    fun goToLogin(){
        val intent : Intent = Intent(applicationContext,Login::class.java)
        startActivity(intent)
    }


    fun goToMain(){
        val intent : Intent = Intent(applicationContext,MainActivity::class.java)
        startActivity(intent)
    }
}
