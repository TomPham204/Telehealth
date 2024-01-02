package com.example.telehealth.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.telehealth.AdminActivity
import com.example.telehealth.DoctorActivity
import com.example.telehealth.MainActivity
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.databinding.LoginScreenBinding
import com.example.telehealth.viewmodel.ProfileViewModel
import java.util.Date

class LoginFragment : Fragment() {

    private var _binding: LoginScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel

    // Email and password for validation
    private var emailToValidate: String? = null
    private var passwordToValidate: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = LoginScreenBinding.inflate(inflater, container, false)
        val profileFactory = ViewModelFactory(requireContext())
        profileViewModel = ViewModelProvider(this, profileFactory)[ProfileViewModel::class.java]

        setupProfileObserver()
        profileViewModel.getAllProfiles()

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

    private fun setupProfileObserver() {
        profileViewModel.allProfiles.observe(viewLifecycleOwner) { profiles ->
            validateCredentialsWithList(profiles)
        }
    }

    private fun validateCredentialsWithList(profiles: List<ProfileModel>) {
        val email = emailToValidate ?: return
        val password = passwordToValidate ?: return

        val validatedProfile = if (email == "admin@gmail.com" && password == "test") {
            ProfileModel("0", email, password, "ADMIN", "admin-name", "admin addr", Date(), "MALE", "")
        } else {
            profiles.firstOrNull { it.email == email && it.password == password }
        }

        handleLoginResult(validatedProfile)
    }

    private fun processLogin() {
        emailToValidate = binding.emailLoginText.text.toString().trim()
        passwordToValidate = binding.passwordLoginText.text.toString().trim()

        if (emailToValidate.isNullOrEmpty() || passwordToValidate.isNullOrEmpty()) {
            Toast.makeText(activity, "Please enter email and password", Toast.LENGTH_LONG).show()
            return
        }

        profileViewModel.getAllProfiles()  // This will trigger the LiveData update
    }

    private fun handleLoginResult(profile: ProfileModel?) {
        if (profile != null) {
            // Credentials are valid, handle the authenticated user
            saveLoginStatus(profile.userId)

            if(profile.functionality == "ADMIN") {
                val intent = Intent(requireActivity(), AdminActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            } else if(profile.functionality == "DOCTOR") {
                val intent = Intent(requireActivity(), DoctorActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            else {
                (activity as? MainActivity)?.replaceFragment(ProfileFragment())
            }
        } else {
            // Credentials are invalid, handle the error
            Toast.makeText(activity, "Incorrect email or password", Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToSignup() {
        (activity as? MainActivity)?.replaceFragment(SignupFragment())
    }

    private fun saveLoginStatus(userId: String) {
        profileViewModel.setCurrentId(userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
