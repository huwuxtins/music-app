package com.example.musicapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.R
import java.util.regex.Pattern


class RegisterEmail : AppCompatActivity() {

    val EMAIL_ADDRESS: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )



    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_email)

        var img_backStart : ImageView = findViewById(R.id.img_backStart)
        var btn_nextPass : Button = findViewById(R.id.btn_nextPass)
        var edt_email :EditText = findViewById(R.id.edt_email);


        val sharedPreference =  getSharedPreferences("AccountTmp", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

        img_backStart.setOnClickListener {
            onBackPressed()
        }

        btn_nextPass.setOnClickListener {

            var email = edt_email.text.toString();

            if(email.equals("")){
                Toast.makeText(
                    applicationContext,
                    "Please enter your email",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
                if(isValidEmail(email)==false){
                    Toast.makeText(
                        applicationContext,
                        "Format email is not correct",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    editor.putString("email",email.toString());
                    editor.commit();
                    var intent = Intent(this, RegisterPassword::class.java)
                    startActivity(intent)
                }
            }


        }



    }
}