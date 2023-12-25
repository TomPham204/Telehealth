package com.example.telehealth.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.telehealth.AdminActivity
import com.example.telehealth.MainActivity
import com.example.telehealth.R
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.databinding.LoginScreenBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

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
        if(email=="admin@admin.admin" && password=="admin") {
            return ProfileModel("0", "admin@admin.admin","admin","ADMIN","admin","", Date("1/1/2001"),"MALE","")
        }

        fun retrieveUsers(): MutableList<ProfileModel> {
            val usersList = mutableListOf<ProfileModel>()
            val sharedPreferences = requireContext().getSharedPreferences("Users", Context.MODE_PRIVATE)
            val usersJson = sharedPreferences.getString("usersKey", null)

            usersJson?.let {
                val type = object : TypeToken<List<ProfileModel>>() {}.type
                usersList.addAll(Gson().fromJson(it, type))
            }

            return usersList
        }

        try {
            val users = retrieveUsers()
            for(i in users) {
                if(i.email==email && i.password==password) {
                    return i
                }
            }
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
        val email = view?.findViewById<EditText>(R.id.emailLoginText)?.text.toString()
        val password = view?.findViewById<EditText>(R.id.passwordLoginText)?.text.toString()

        val user=validateCredential(email, password)

        if(user != null) {
            // save active userId to local storage
            saveLoginStatus(user.userId)

            if(user.functionality=="ADMIN") {
                val intent = Intent(requireActivity(), AdminActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            } else {
                (activity as? MainActivity)?.replaceFragment(ProfileFragment())
            }

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
