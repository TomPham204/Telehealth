package com.example.telehealth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.telehealth.fragment.AdminAppointmentFragment
import com.example.telehealth.fragment.AdminProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        bottomNavigationView = findViewById(R.id.admin_navigation)

        // Set default fragment when the app starts
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.admin_fragment_container, AdminProfileFragment())
                .commit()
        }

        // Handle item clicks in the bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    replaceFragment(AdminProfileFragment())
                    true
                }
                R.id.menu_appointment -> {
                    replaceFragment(AdminAppointmentFragment())
                    true
                }
                R.id.menu_logout -> {
                    val sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("USER_ID", null)
                        apply()
                    }
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                // Add logic for other menu items if needed
                else -> false
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.admin_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
