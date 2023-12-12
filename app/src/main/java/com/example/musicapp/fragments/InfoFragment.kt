package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.example.musicapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class InfoFragment: Fragment(R.layout.fragment_info) {
    private lateinit var imgAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnEdit: Button
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        imgAvatar = view.findViewById(R.id.imgAvatar)
        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        btnEdit = view.findViewById(R.id.btnEdit)

        auth = FirebaseAuth.getInstance()
        db =  FirebaseFirestore.getInstance()

        showInfo()

        return view
    }



    fun showInfo(){
        val user = auth.currentUser
        val email = user?.providerData?.get(1)?.email
        val docRef: DocumentReference = db.collection("Users").document(email.toString())
        docRef.get().addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val document = task.result

                if(document.exists()){
                    val u = document.toObject(User::class.java)
                    tvName.setText(u?.username)
                    tvEmail.setText(email.toString() )
                }
                else{
                    Toast.makeText(activity,"Account data does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(activity,"Error system", Toast.LENGTH_SHORT).show()
            }
        }

    }
}