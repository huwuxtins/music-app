package com.example.musicapp.activities

import android.health.connect.datatypes.units.Length
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.musicapp.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class EnterLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_login)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        var firebaseAuth = FirebaseAuth.getInstance();

        var img_back : ImageView ?= null
        var edt_email : EditText ?= null
        var edt_password : EditText ?= null
        var btn_login : Button ?= null

        edt_email = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_login);
        img_back = findViewById(R.id.img_back)

        img_back.setOnClickListener {
            onBackPressed();
        }


        btn_login.setOnClickListener {
            var email = edt_email.text.toString();
            var password = edt_password.text.toString();

            if (email.equals("") || password.equals("")) {
                Toast.makeText(
                    applicationContext,
                    "Please enter complete information to log in",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Logged in successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Email or password is incorrect",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
            }
        }


    }
}