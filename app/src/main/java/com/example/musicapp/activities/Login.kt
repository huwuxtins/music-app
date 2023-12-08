package com.example.musicapp.activities

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.R
import com.example.musicapp.dialog.LoadingDialog
import com.example.musicapp.models.User
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore


class Login : AppCompatActivity() {

    lateinit var db : FirebaseFirestore
    lateinit var auth : FirebaseAuth
    lateinit var googleSignIn : GoogleSignInClient
    lateinit var dialog : LoadingDialog
    lateinit var callbackManager : CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var txt_login : TextView ?= null
        var btn_signup : Button = findViewById(R.id.btn_signup)
        var btn_google : Button = findViewById(R.id.btn_google)
        var btn_facebook : Button = findViewById(R.id.btn_facebook)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        dialog = LoadingDialog(this)
        callbackManager = CallbackManager.Factory.create()

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


        btn_facebook.setOnClickListener{
            signInWithFacebook()
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
        else{
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        dialog.ShowDialog("Google")
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val email = user?.providerData?.get(1)?.email
                    val name = user?.displayName
                    val image =  user?.providerData?.get(1)?.photoUrl
                    val uid = user?.providerData?.get(1)?.uid
                    val nUser = User(name.toString(),email.toString(),"null","Male",true,image.toString(),uid.toString())

                    db.collection("Users").document(nUser.email).set(nUser)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                            dialog.HideDialog()
                            startActivity(Intent(this, MainActivity::class.java))
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


    fun  signInWithFacebook(){
        LoginManager.getInstance().logInWithReadPermissions(this@Login,listOf("email","public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    if (loginResult != null) {
                        handleFacebookAccessToken(loginResult.accessToken)
                    }
                }
                override fun onCancel() {
                    Toast.makeText(
                        baseContext,
                        "Cancel",
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(
                        baseContext,
                        "Error",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            })
    }


    @SuppressLint("SuspiciousIndentation")
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        dialog.ShowDialog("Facebook")
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val id = user?.providerData?.get(1)?.uid
                    val name = user?.displayName
                    val image =  user?.providerData?.get(1)?.photoUrl
                    val email = user?.providerData?.get(1)?.email
                    val nUser = User(name.toString(),""+email.toString(),"null","Male",true,image.toString(),id.toString())
                    db.collection("Users").document(nUser.email).set(nUser)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(
                                baseContext,
                                "Log in success with " + user?.displayName,
                                Toast.LENGTH_SHORT,
                            ).show()

                             dialog.HideDialog()

                            var intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)

                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(applicationContext,"Failed!!",Toast.LENGTH_SHORT).show()
                            dialog.HideDialog()
                        }

                } else {
                    Toast.makeText(
                        baseContext,
                        "Email of Facebook account already exists.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    dialog.HideDialog()
                }
            }
    }
}