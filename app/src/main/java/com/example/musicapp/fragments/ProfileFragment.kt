package com.example.musicapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity

class ProfileFragment: Fragment(R.layout.fragment_profile) {
    private lateinit var tvInfo: TextView
    private lateinit var tvEdit: TextView
    private lateinit var tvLogout: TextView
    private lateinit var btnSetting: ImageButton

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
                        Log.e("MyApp", "Setting play music")
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
        return view
    }
}