package com.example.musicapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.musicapp.R
import com.example.musicapp.dialog.LoadingDialog
import com.example.musicapp.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {

    lateinit var db : FirebaseFirestore
    lateinit var auth : FirebaseAuth
    lateinit var googleSignIn : GoogleSignInClient
    lateinit var dialog : LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var txt_login : TextView ?= null
        var btn_signup : Button = findViewById(R.id.btn_signup)
        var btn_google : Button = findViewById(R.id.btn_google)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        dialog = LoadingDialog(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("883793367061-r7idev7nonpp6nb1f11nguaa99h7g17l.apps.googleusercontent.com")
            .requestEmail()
            .build()

          googleSignIn = GoogleSignIn.getClient(this, gso)

        txt_login = findViewById(R.id.txt_login)
        txt_login.setOnClickListener {
            val intent = Intent(this, EnterLogin::class.java)
            startActivity(intent)
        }

        btn_signup.setOnClickListener {
            var intent = Intent(this, RegisterEmail::class.java)
            startActivity(intent)
        }

        btn_google.setOnClickListener {
            signInWithGoogle()
        }


    }

    fun signInWithGoogle(){
        val intent = googleSignIn.signInIntent
        startActivityForResult(intent,111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 111){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
               // Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        dialog.ShowDialog("Google")
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val email = user?.providerData?.get(1)?.email
                    val name = user?.displayName

                    val nUser = User(name.toString(),email.toString(),"null","Male",true,"image.jpg")

                    db.collection("Users").document(nUser.email).set(nUser)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            dialog.HideDialog()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(applicationContext,"Failed!!",Toast.LENGTH_SHORT).show()
                            dialog.HideDialog()
                        }

                } else {
                    Toast.makeText(this, "Authentication Google failed", Toast.LENGTH_SHORT).show()
                    dialog.HideDialog()

                }
            }
    }
}