package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.musicapp.R
import com.example.musicapp.adapters.AccountSettingViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AccountSettingFragment() : Fragment() {
    private lateinit var imgAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var vpEdit: ViewPager2
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)
        imgAvatar = view.findViewById(R.id.imgAvatar)
        tvName = view.findViewById(R.id.tvName)
        tabLayout = view.findViewById(R.id.tabLayout)
        vpEdit = view.findViewById(R.id.vpEdit)
        auth = FirebaseAuth.getInstance()
        db =FirebaseFirestore.getInstance()


        val fragmentList = listOf(EditInformationFragment(), ChangePasswordFragment())

        vpEdit.adapter = activity?.let {
            AccountSettingViewPagerAdapter(fragmentList,it.supportFragmentManager,lifecycle)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    vpEdit.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        updateUI()

        return view
    }

    fun updateUI(){
        val fUser = auth.currentUser
        val email = fUser?.email.toString()
        val docRef = db.collection("Users").document(email)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(com.example.musicapp.models.User::class.java)
            tvName.text = user?.username

            val linkImg = user?.avatar.toString()
            if(linkImg.contains("https://")){
                Picasso.get().load(linkImg).into(imgAvatar);
            }


        }

    }

}