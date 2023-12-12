package com.example.musicapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.R
import com.example.musicapp.dialog.LoadingDialog
import com.example.musicapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern


class ForgotPassword : AppCompatActivity() {

    lateinit var img_back : ImageView
    lateinit var btn_next : Button
    lateinit var edt_email : EditText
    lateinit var db : FirebaseFirestore
    lateinit var dialog : LoadingDialog
    lateinit var auth : FirebaseAuth


    val EMAIL_ADDRESS: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )




    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        img_back = findViewById(R.id.img_backStart)
        btn_next = findViewById(R.id.btn_nextPass)
        edt_email = findViewById(R.id.edt_email)
        db = FirebaseFirestore.getInstance()
        dialog = LoadingDialog(this)
        auth = FirebaseAuth.getInstance()
        img_back.setOnClickListener {
            onBackPressed()
        }

        btn_next.setOnClickListener {
            var email = edt_email.text.toString()
            if(email.equals("")){
                Toast.makeText(applicationContext,"Please enter your email",Toast.LENGTH_SHORT).show()
            }
            else{
                if(isValidEmail(email) == false){
                    Toast.makeText(applicationContext,"Format email invalid",Toast.LENGTH_SHORT).show()
                }
                else{
                    dialog.ShowDialog("checkEmail")
                    val docRef: DocumentReference = db.collection("Users").document(email)
                    docRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if(document != null) {
                                if (document.exists()) {
                                    val user = document.toObject(User::class.java)
                                    if(user?.type.toString()=="Normal"){
                                        auth.sendPasswordResetEmail(email)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(applicationContext,"Password reset email successfully sent",Toast.LENGTH_SHORT).show();
                                                    dialog.HideDialog()
                                                    var intent = Intent(this, EnterLogin::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                                else{
                                                    Toast.makeText(applicationContext,"Sending password reset email failed",Toast.LENGTH_SHORT).show();
                                                    dialog.HideDialog()
                                                }
                                            }
                                    }
                                    else{
                                        Toast.makeText(applicationContext,"This account cannot perform forgotten password",Toast.LENGTH_SHORT).show();
                                        dialog.HideDialog()
                                    }

                                } else {
                                    Toast.makeText(applicationContext,"Email does not exist",Toast.LENGTH_SHORT).show();
                                    dialog.HideDialog()
                                }
                            }
                        } else {
                            Toast.makeText(applicationContext,"Error system",Toast.LENGTH_SHORT).show();
                            dialog.HideDialog()
                        }
                    }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }
}