package com.example.musicapp.fragments

import android.os.Bundle
import android.service.autofill.FillRequest
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.example.musicapp.dialog.LoadingDialog
import com.example.musicapp.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditInformationFragment(): Fragment() {
    private lateinit var btnSave: Button
    private lateinit var inpUsername : EditText
    private lateinit var gender : Spinner
    private lateinit var confirmPass : EditText
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var dialog : LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var genderArr = arrayOf("Male","Female")
        val view = inflater.inflate(R.layout.fragment_edit_information, container, false)
        btnSave = view.findViewById(R.id.btnSave)
        inpUsername = view.findViewById(R.id.inpUsername)
        gender = view.findViewById(R.id.spinner)
        confirmPass = view.findViewById(R.id.inpConfPass)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        dialog = LoadingDialog(requireActivity())

        gender.adapter = context?.let {
            ArrayAdapter(
                it,
                R.layout.spinner_item2,
                genderArr)

        }

        val fUser = auth.currentUser
        val email = fUser?.email.toString()
        val docRef = db.collection("Users").document(email)


        docRef.get().addOnSuccessListener { documentSnapshot->
                val user = documentSnapshot.toObject(User::class.java)
                inpUsername.setText(user?.username)
                gender.setSelection(genderArr.indexOf(user?.gender))
        }

        btnSave.setOnClickListener{
            updateProfile()
        }

        return view
    }

    fun updateProfile(){

        val getUserName = inpUsername.text.toString().trim()
        val getSpinner = gender.selectedItem.toString()
        val getPass = confirmPass.text.toString()
        dialog.ShowDialog("Update Profile")
        if(getUserName == "" ||getPass== ""){
            dialog.HideDialog()
            Toast.makeText(context,"Please enter username and password to update profile",Toast.LENGTH_SHORT).show()
        }
        else{
            val fUser = auth.currentUser
            val email = fUser?.email.toString()
            val docRef = db.collection("Users").document(email)
            docRef.get().addOnSuccessListener { documentSnapshot->
                val user = documentSnapshot.toObject(User::class.java)
                if(user?.password.toString() == getPass){
                    docRef.update( mapOf("username" to getUserName, "gender" to getSpinner))
                        .addOnSuccessListener {
                            confirmPass.setText("")
                            dialog.HideDialog()
                            Toast.makeText(context,"Update successfully",Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener{
                            dialog.HideDialog()
                            Toast.makeText(context,"Update failed",Toast.LENGTH_SHORT).show()

                        }
                }
                else{
                    dialog.HideDialog()
                    Toast.makeText(context,"Incorrect password",Toast.LENGTH_SHORT).show()

                }

            }
        }


    }
}