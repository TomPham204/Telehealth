package com.example.telehealth.fragment

import android.net.ParseException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.telehealth.MainActivity
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.databinding.SignupScreenBinding
import com.example.telehealth.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class SignupFragment : Fragment() {

    private var _binding: SignupScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = SignupScreenBinding.inflate(inflater, container, false)
        val profileFactory = ViewModelFactory(requireContext())
        profileViewModel = ViewModelProvider(this, profileFactory)[ProfileViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signupButton.setOnClickListener {
            processSignup()
        }

        binding.toLoginButton.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun processSignup() {
        val email = binding.emailSignupText.text.toString().trim()
        val password = binding.passwordSignupText.text.toString().trim()
        val functionality = binding.functionalitySignupText.selectedItem.toString().trim()
        val name = binding.nameSignupText.text.toString().trim()
        val address = binding.addressSignupText.text.toString().trim()
        val gender = binding.genderSignupText.selectedItem.toString().trim()
        val description = binding.descriptionSignupText.text.toString().trim()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var _dateOfBirth: Date? = null
        try {
            _dateOfBirth = dateFormat.parse(binding.dateOfBirthSignupText.text.toString().trim())
        } catch (e: ParseException) {
            Toast.makeText(context, "Invalid Date Format", Toast.LENGTH_SHORT).show()
            return
        }

        // check if user exists
        var currentUsers = retrieveUsers()

        for (i in currentUsers) {
            if(i.email.equals(email, true)) {
                Toast.makeText(activity, "User already existed", Toast.LENGTH_SHORT).show()
                return
            }
        }


        // now do if else
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || address.isEmpty() || gender.isEmpty() || description.isEmpty()) {
            Toast.makeText(activity, "Please check your input", Toast.LENGTH_SHORT).show()
        } else {
            try {
                // register user
                currentUsers.add(ProfileModel(UUID.randomUUID().toString(), email, password, functionality, name, address, _dateOfBirth, gender, description))
                saveUsers(currentUsers)

            } catch (error: Exception) {
                Toast.makeText(activity, "Network error", Toast.LENGTH_SHORT).show()
            }
        }
        navigateToLogin()
    }

    private fun retrieveUsers(): MutableList<ProfileModel> {
        return profileViewModel.getAllProfiles().toMutableList()
    }

    private fun saveUsers(newList: List<ProfileModel>) {
        profileViewModel.setProfiles(newList)
    }

    private fun navigateToLogin() {
        (activity as? MainActivity)?.replaceFragment(LoginFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
