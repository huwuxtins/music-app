package com.example.musicapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.musicapp.R
import com.example.musicapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterAccount : AppCompatActivity() {

    var gender = arrayOf("Male","Female")
    var firebaseAuth = FirebaseAuth.getInstance();
    lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_account)


        var chooseGender : Spinner ?= null
        chooseGender = findViewById(R.id.spinner);

        val adapter = ArrayAdapter(this,
            R.layout.spinner_item, gender)
        chooseGender.adapter = adapter;


        var edt_name : EditText = findViewById(R.id.edt_name);
        var btn_final : Button = findViewById(R.id.btn_final);

        val sharedPreference =  getSharedPreferences("AccountTmp", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()


        btn_final.setOnClickListener {
            var name = edt_name.text.toString()
            var email = sharedPreference.getString("email","null");
            var email2 = email?.replace('.','-');
            var password = sharedPreference.getString("password","null");
            var gender = chooseGender.selectedItem.toString();

            if(name.equals("")){
                Toast.makeText(applicationContext,"Please enter your name",Toast.LENGTH_SHORT).show()
            }
            else{
                firebaseAuth.createUserWithEmailAndPassword(email.toString(),password.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {


                            database = FirebaseDatabase.getInstance().getReference("Users")

                            var user = User(name.toString(),email.toString(),password.toString(),gender.toString(),true,"image.jpg");



                            database.child(email2.toString()).setValue(user) .addOnSuccessListener {
                                Toast.makeText(applicationContext,"Successfully create account",Toast.LENGTH_SHORT).show()
                                editor.clear()
                                var intent = Intent(this, EnterLogin::class.java)
                                startActivity(intent)
                            }.addOnFailureListener{
                                Toast.makeText(applicationContext,"Failed!!",Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(applicationContext,"Account already exists",Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }

    }
}