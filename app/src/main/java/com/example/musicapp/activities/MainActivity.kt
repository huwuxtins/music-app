package com.example.musicapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.example.musicapp.fragments.HomeFragment
import com.example.musicapp.fragments.LibraryFragment
import com.example.musicapp.fragments.ProfileFragment
import com.example.musicapp.fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.btNav)

        val homeFragment = HomeFragment()
        val searchFragment = SearchFragment()
        val libraryFragment = LibraryFragment()
        val profileFragment = ProfileFragment()

        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.action_home -> {
                    loadFragment(homeFragment)
                    true
                }
                R.id.action_search -> {
                    loadFragment(searchFragment)
                    true
                }
                R.id.action_library -> {
                    loadFragment(libraryFragment)
                    true
                }
                R.id.action_profile -> {
                    loadFragment(profileFragment)
                    true
                }
                else -> {
                    loadFragment(homeFragment)
                    true
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frgMain, fragment)
        transaction.commit()
    }
}