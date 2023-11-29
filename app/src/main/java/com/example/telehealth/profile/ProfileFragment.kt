package com.example.telehealth.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.telehealth.MainActivity
import com.example.telehealth.R
import com.example.telehealth.databinding.ProfileFragmentBinding
import com.example.telehealth.databinding.LoginScreenBinding

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private var isLoggedIn = false
    private lateinit var binding: ViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isLoggedIn=checkLoginStatus()
        return if (isLoggedIn) {
            val profileBinding: ProfileFragmentBinding = DataBindingUtil.inflate(
                inflater, R.layout.profile_fragment, container, false
            )
            displayProfileInfo(profileBinding)

            profileBinding.logoutButton.setOnClickListener {
                isLoggedIn=false
                saveLoginStatus(isLoggedIn)
                remountProfileFragment()
            }

            profileBinding.root
        } else {
            binding = LoginScreenBinding.inflate(inflater, container, false)
            showLoginForm()
            binding.root
        }
    }

    private fun checkLoginStatus(): Boolean {
        return try {
            val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
            sharedPreferences.getBoolean("IS_LOGGED_IN", false)
        } catch(error: Exception) {
            Log.d("checkLoginStatus", error.toString())
            false
        }
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("IS_LOGGED_IN", isLoggedIn)
            apply()
        }
    }

    private fun showLoginForm() {
        val loginBinding = binding as? LoginScreenBinding

        loginBinding?.submitButton?.setOnClickListener {
            val email = loginBinding.emailEditText.text.toString()
            val password = loginBinding.passwordEditText.text.toString()

            if (email == "admin@gmail.com" && password == "admin") {
                isLoggedIn = true
                saveLoginStatus(isLoggedIn)
                Log.d("showLoginForm","Login details correct")
                remountProfileFragment()
            } else {
                Toast.makeText(requireContext(), "Unrecognized account", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun remountProfileFragment() {
        try {
            val mainActivity = activity as? MainActivity
            mainActivity?.replaceFragment(ProfileFragment())
        }
        catch(error: Exception) {
            Log.d("remountProfileFragment", error.toString())
        }
    }

    private fun displayProfileInfo(binding: ProfileFragmentBinding) {
        // Display current profile information
        // Fetch and display user profile data or a default profile
        val userProfile = getUserProfile()
        binding.textViewName.text = "Name: ${userProfile.userName}"
        binding.textViewEmail.text = "Email: ${userProfile.userEmail}"
        binding.textViewPhone.text = "Phone: ${userProfile.userPhoneNumber}"
    }

    private fun getUserProfile(): UserProfile {
        // Replace this with actual profile retrieval logic
        return UserProfile(
            userId = "0",
            userName = "Tuan",
            userEmail = "tuan@example.com",
            userPhoneNumber = "+1234567890"
        )
    }
}