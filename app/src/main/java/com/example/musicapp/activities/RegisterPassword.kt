package com.example.musicapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.musicapp.R
import com.example.musicapp.dialog.LoadingDialog
import com.example.musicapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPassword : AppCompatActivity() {

    lateinit var dialog : LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_password)

        var img_backEmail : ImageView = findViewById(R.id.img_backEmail)
        var edt_password : EditText = findViewById(R.id.edt_password);
        var btn_nextname : Button = findViewById(R.id.btn_nextName)

        val sharedPreference =  getSharedPreferences("AccountTmp", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

        dialog = LoadingDialog(this)

        img_backEmail.setOnClickListener {
            onBackPressed()
        }

        btn_nextname.setOnClickListener {
            var password = edt_password.text.toString();

            if (password.length < 8) {
                Toast.makeText(
                    applicationContext,
                    "Password length is at least 8 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                editor.putString("password", password);
                editor.commit();
                var intent = Intent(this, RegisterAccount::class.java)
                startActivity(intent)
            }
        }
    }
}