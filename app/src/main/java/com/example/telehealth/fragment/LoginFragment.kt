package com.example.telehealth.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.telehealth.MainActivity
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.databinding.LoginScreenBinding

class LoginFragment : Fragment() {

    private var _binding: LoginScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = LoginScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            processLogin()
        }

        binding.toSignupButton.setOnClickListener {
            navigateToSignup()
        }
    }

    private fun validateCredential(email: String, password: String): ProfileModel? {
        // call DB to check if credential is correct and exist a user
        // return the user if exists
        try {
            return null
        } catch (error: Exception) {
            return null
        }
    }

    private fun saveLoginStatus(userId: String) {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("USER_ID", userId)
            apply()
        }
    }

    private fun processLogin() {
        // retrieve email and password from the login form
        val email = binding.emailLoginText.text.toString()
        val password = binding.passwordLoginText.text.toString()

        val user=validateCredential(email, password)

        if(user != null) {
            // save active userId to local storage
            saveLoginStatus(user.userId)

            // direct to Profile page
            (activity as? MainActivity)?.replaceFragment(ProfileFragment())
        } else {
            //show toast of incorrect credential
            Toast.makeText(activity, "Incorrect email or password", Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToSignup() {
        (activity as? MainActivity)?.replaceFragment(SignupFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
