package com.example.telehealth.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.telehealth.AdminActivity
import com.example.telehealth.MainActivity
import com.example.telehealth.R
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.databinding.LoginScreenBinding
import com.example.telehealth.viewmodel.ProfileViewModel
import java.util.Date

class LoginFragment : Fragment() {

    private var _binding: LoginScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = LoginScreenBinding.inflate(inflater, container, false)
        val profileFactory = ViewModelFactory(requireContext())
        profileViewModel = ViewModelProvider(this, profileFactory)[ProfileViewModel::class.java]

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
        if(email=="admin@gmail.com" && password=="test") {
            return ProfileModel("0", "admin@gmail.com","test","ADMIN","admin-name","admin addr", Date("1/1/2001"),"MALE","")
        }

        try {
            val users = profileViewModel.getAllProfiles()
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
        profileViewModel.setCurrentId(userId)
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
