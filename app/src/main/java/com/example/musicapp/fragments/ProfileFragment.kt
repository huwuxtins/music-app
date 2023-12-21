package com.example.musicapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.example.musicapp.activities.Login
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.dialog.LoadingDialog
import com.example.musicapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileFragment: Fragment(R.layout.fragment_profile) {
    private lateinit var tvInfo: TextView
    private lateinit var tvEdit: TextView
    private lateinit var tvLogout: TextView
    private lateinit var btnSetting: ImageButton
    lateinit var tvLibrary : TextView
    lateinit var tvMyPage: TextView
    lateinit var imgAvatar : ImageView
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var dialog : LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        tvInfo = view.findViewById(R.id.tvInfo)
        tvEdit = view.findViewById(R.id.tvEdit)
        tvLogout = view.findViewById(R.id.tvLogout)
        btnSetting = view.findViewById(R.id.btnSetting)
        auth = FirebaseAuth.getInstance()
        db =FirebaseFirestore.getInstance()
        tvLibrary = view.findViewById(R.id.tvLibrary)
        tvMyPage = view.findViewById(R.id.tvMyPage)
        imgAvatar = view.findViewById(R.id.imgAvatar)

        val mainActivity = context as MainActivity
        tvInfo.setOnClickListener{
            mainActivity.loadFragment(InfoFragment(), "body")
        }
        tvEdit.setOnClickListener{
            mainActivity.loadFragment(AccountSettingFragment(), "body")
        }

        btnSetting.setOnClickListener { view ->
            val popupMenu = PopupMenu(context, view)
            popupMenu.inflate(R.menu.menu_setting) // Inflate your menu resource
            popupMenu.show()

            // Set click listeners for menu items (if needed)
            popupMenu.setOnMenuItemClickListener {
                // Handle menu item click here
                when (it.itemId) {
                    R.id.itPlay -> {
                        mainActivity.loadFragment(SettingFragment(), "main")
                        true
                    }

                    R.id.itContact -> {
                        Log.e("MyApp", "Setting contact")
                        true
                    }

                    else -> false
                }
            }
        }

        tvLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, Login::class.java)
            startActivity(intent)
            activity?.finish()
        }

        getInformation()

        return view
    }

    private fun getInformation(){
        val fUser = auth.currentUser
        val email = fUser?.email.toString()

        val docRef = db.collection("Users").document(email)

        docRef.get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.toObject(User::class.java)
            tvLibrary.setText(user?.username)
            tvMyPage.setText(user?.email)
            Picasso.get().load(user?.avatar).into(imgAvatar)
        }


    }
}