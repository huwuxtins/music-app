package com.example.musicapp.activities

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.musicapp.R
import com.example.musicapp.fragments.HomeFragment
import com.example.musicapp.fragments.LibraryFragment
import com.example.musicapp.fragments.ProfileFragment
import com.example.musicapp.fragments.SearchFragment
import com.example.musicapp.fragments.SongFragment
import com.example.musicapp.models.Song
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    Toast.makeText(this, "Thanks for your access", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        "Please access permission to use our feature",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        // Request permission when the activity is created
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_MEDIA_LOCATION)
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

        bottomNav = findViewById(R.id.btNav)

        val homeFragment = HomeFragment()
        val searchFragment = SearchFragment()
        val libraryFragment = LibraryFragment()
        val profileFragment = ProfileFragment()

        loadFragment(homeFragment, "main")

        if(intent.action == "HOME_MUSIC"){
            Log.e("MyApp", "HOME_MUSIC")
            val song = intent.extras?.getSerializable("song") as? Song
            val songs = intent.extras?.getSerializable("songs") as? ArrayList<Song>
            if(songs != null){
                Log.e("MyApp", "Song's name")
                if(song != null){
                    Log.e("MyApp", "Song's name: ${song.name}")
                    loadFragment(SongFragment(song, songs), "body")
                }
            }
        }
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
            else if(stackName == "music"){
                this.setCustomAnimations(
                    R.anim.slide_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_down
                )
            }

            replace(R.id.frgMain, fragment)
            if(stackName != "music"){
                addToBackStack(stackName)
            }
        }
    }
}