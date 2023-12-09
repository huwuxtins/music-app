package com.example.musicapp.activities

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.example.musicapp.R
import com.example.musicapp.fragments.HomeFragment
import com.example.musicapp.fragments.LibraryFragment
import com.example.musicapp.fragments.ProfileFragment
import com.example.musicapp.fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.btNav)
        mediaPlayer = MediaPlayer()

        val homeFragment = HomeFragment()
        val searchFragment = SearchFragment()
        val libraryFragment = LibraryFragment()
        val profileFragment = ProfileFragment()

        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.action_home -> {
                    loadFragment(homeFragment, "main")
                    true
                }
                R.id.action_search -> {
                    loadFragment(searchFragment, "main")
                    true
                }
                R.id.action_library -> {
                    loadFragment(libraryFragment, "main")
                    true
                }
                R.id.action_profile -> {
                    loadFragment(profileFragment, "main")
                    true
                }
                else -> {
                    loadFragment(homeFragment, "main")
                    true
                }
            }
        }


    }

    fun loadFragment(fragment: Fragment, stackName: String){
        supportFragmentManager.commit {
            if(stackName == "main"){
                this.setCustomAnimations(
                    R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out
                )
            }
            else if(stackName == "body"){
                this.setCustomAnimations(
                    R.anim.slide_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_down
                )
            }

            replace(R.id.frgMain, fragment)
            addToBackStack(stackName)
        }
    }
}