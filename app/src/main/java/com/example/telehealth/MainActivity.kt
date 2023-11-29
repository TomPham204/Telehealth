package com.example.telehealth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.telehealth.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set default fragment (ProfileFragment) when the app starts
        if (savedInstanceState == null) {
            val profileFragment = ProfileFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, profileFragment)
                .commit()
        }

        // Handle item clicks in the bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                R.id.menu_video -> {
                    startVideoActivity()
                    true
                }
                // Add logic for other menu items if needed
                else -> false
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun startVideoActivity() {
        try {
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        } catch(e: Exception) {
            Log.d("startVideoActivity", e.toString())
        }

    }
}
