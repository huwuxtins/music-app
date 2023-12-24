package com.example.musicapp.fragments

import android.R.attr.name
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.example.musicapp.dialog.LoadingDialog
import com.example.musicapp.models.Artist
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User


class ChangePasswordFragment(): Fragment() {
    private lateinit var inpCurrPass: TextInputEditText
    private lateinit var inpNewPass: TextInputEditText
    private lateinit var inpConfPass: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    lateinit var dialog : LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)
        inpCurrPass = view.findViewById(R.id.inpCurrPass)
        inpNewPass = view.findViewById(R.id.inpNewPass)
        inpConfPass = view.findViewById(R.id.inpConfPass)
        btnSave = view.findViewById(R.id.btnSave)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        dialog = LoadingDialog(requireActivity())

        btnSave.setOnClickListener{
            changePassword()

        }

        return view
    }


    fun changePassword(){
        val fUser = auth.currentUser
        val email = fUser?.email.toString()
        val docRef = db.collection("Users").document(email)

        val cPassInput =  inpCurrPass.text.toString().trim()
        val nPassInput = inpNewPass.text.toString().trim()
        val cfPassInput = inpConfPass.text.toString().trim()

        dialog.ShowDialog("Change Password")
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(com.example.musicapp.models.User::class.java)

            val type = user?.type

            if(type != "Normal"){
                Toast.makeText(context,"Cannot change the password for this account",Toast.LENGTH_SHORT).show()
                dialog.HideDialog()
            }
            else{
                if(cPassInput == "" || nPassInput == "" || cfPassInput == ""){
                    Toast.makeText(context,"Please enter complete information",Toast.LENGTH_SHORT).show()
                    dialog.HideDialog()
                }
                else{
                    if (nPassInput != cfPassInput){
                        Toast.makeText(context,"Please confirm your password again",Toast.LENGTH_SHORT).show()
                        dialog.HideDialog()
                    }
                    else{
                        val pass = user?.password
                        if(pass != cPassInput){
                            Toast.makeText(context,"Account password is incorrect",Toast.LENGTH_SHORT).show()
                            dialog.HideDialog()
                        }
                        else{
                            docRef
                                .update("password",nPassInput)
                                .addOnSuccessListener {
                                    if(fUser != null) {
                                        fUser.updatePassword(nPassInput)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(context,"Update password successfully",Toast.LENGTH_SHORT).show()
                                                    dialog.HideDialog()
                                                } else{
                                                    docRef.update("password",cPassInput)
                                                    Toast.makeText(context,"Password change failed with Firebase", Toast.LENGTH_SHORT).show()
                                                    dialog.HideDialog()
                                                }
                                            }
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context,"Password change failed",Toast.LENGTH_SHORT).show()
                                    dialog.HideDialog()

                                }

                        }
                    }
                }
            }



        }
            .addOnFailureListener{ ex ->
                Toast.makeText(context,"Error System", Toast.LENGTH_SHORT).show()
                dialog.HideDialog()
            }

    }
}