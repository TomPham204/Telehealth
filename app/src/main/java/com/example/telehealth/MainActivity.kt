package com.example.telehealth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.telehealth.fragment.AppointmentFragment
import com.example.telehealth.fragment.ChatFragment
import com.example.telehealth.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set default fragment when the app starts
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }

        // Handle item clicks in the bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                R.id.menu_appointment -> {
                    replaceFragment(AppointmentFragment())
                    true
                }
                R.id.menu_chat -> {
                    replaceFragment(ChatFragment())
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

}
