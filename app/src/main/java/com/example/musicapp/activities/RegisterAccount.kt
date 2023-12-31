package com.example.musicapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.musicapp.R
import com.example.musicapp.controllers.PlaylistController
import com.example.musicapp.dialog.LoadingDialog
import com.example.musicapp.models.Playlist
import com.example.musicapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class RegisterAccount : AppCompatActivity() {

    private var gender = arrayOf("Male","Female")
    private var firebaseAuth = FirebaseAuth.getInstance();
    private val playlistController = PlaylistController()
    //late init var database : DatabaseReference
    lateinit var db : FirebaseFirestore
    lateinit var dialog : LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_account)


        var chooseGender : Spinner ?= null
        chooseGender = findViewById(R.id.spinner);

        val adapter = ArrayAdapter(this,
            R.layout.spinner_item, gender)
        chooseGender.adapter = adapter;

        dialog = LoadingDialog(this)

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
                dialog.ShowDialog("Register...")
                firebaseAuth.createUserWithEmailAndPassword(email.toString(),password.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
//                            database = FirebaseDatabase.getInstance().getReference("Users")
                            db = FirebaseFirestore.getInstance()
                            val linkAvatar = resources.getString(R.string.defaultAvatar);
                            val user = User(name.toString(),email.toString(),password.toString(),gender.toString(),true,linkAvatar,email.toString(),"Normal",null);
                            val newUser: HashMap<String, Any>  = user.toMap()
                            db.collection("Users").document(user.email).set(newUser)
                                .addOnSuccessListener { documentReference ->
                                    Toast.makeText(applicationContext,"Account successfully created",Toast.LENGTH_SHORT).show()
                                    editor.clear()
                                    dialog.HideDialog()

//                                    Create for user 2 playlist: lovely and listened
                                    val lovelyPlaylist = Playlist("Lovely", email!!, LocalDate.now().toString(), "")
                                    playlistController.addPlaylist(lovelyPlaylist, onComplete = {
                                        Log.e("MyApp", "Add Lovely successfully")
                                    }, onFail = {
                                        Log.e("MyApp", "Add Lovely failed")
                                    })

                                    val listenedPlaylist = Playlist("ListenedPlaylist", email, LocalDate.now().toString(), "")
                                    playlistController.addPlaylist(listenedPlaylist, onComplete = {
                                        Log.e("MyApp", "Add Listened successfully")
                                    }, onFail = {
                                        Log.e("MyApp", "Add Listened failed")
                                    })

                                    val intent = Intent(this, EnterLogin::class.java)
                                    startActivity(intent)
                                    finish()
                                 }
                                .addOnFailureListener {
                                    Toast.makeText(applicationContext,"Failed!!",Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(applicationContext,"Email already exists",Toast.LENGTH_SHORT).show()
                            dialog.HideDialog()
                        }
                    }
            }

        }

    }
}