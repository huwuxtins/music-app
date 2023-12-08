package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.google.firebase.auth.FirebaseAuth

class InfoFragment: Fragment(R.layout.fragment_info) {
    private lateinit var imgAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnEdit: Button
    private lateinit var auth : FirebaseAuth

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

        updateInfo()

        return view
    }



    fun updateInfo(){
        val user = auth.currentUser
        val email = user?.providerData?.get(1)?.email
        val name = user?.displayName

        tvName.setText(name.toString())
        tvEmail.setText(email.toString() )

    }
}