package com.example.telehealth.fragment

import android.content.Context
import android.net.ParseException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.telehealth.MainActivity
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.databinding.ProfileFragmentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private var user: ProfileModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileData()

        binding.saveButton.setOnClickListener {
            saveProfileData()
        }

        binding.logoutButton.setOnClickListener {
            fun saveLoginStatus(userId: String) {
                val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString("USER_ID", userId)
                    apply()
                }
            }
            saveLoginStatus("")
            (activity as? MainActivity)?.replaceFragment(LoginFragment())
        }
    }

    private fun loadProfileData() {
        // Load the user's profile data and set it in the EditText fields
        // This is a placeholder - replace it with your actual data loading logic
        val userProfile = getUserProfile()

        binding.editTextId.text = userProfile.userId
        binding.editTextEmail.setText(userProfile.email)
        binding.editTextFunctionality.setText(userProfile.functionality)
        binding.editTextName.setText(userProfile.name)
        binding.editTextAddress.setText(userProfile.address)
        binding.editTextDateOfBirth.setText(userProfile.dateOfBirth.toString())
        binding.editTextGender.setText(userProfile.gender)
        binding.editTextDescription.setText(userProfile.description)
        // Don't set the password for security reasons
    }

    private fun saveProfileData() {
        // Save the updated profile data
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var _dateOfBirth: Date? = null

        try {
            _dateOfBirth = dateFormat.parse(binding.editTextDateOfBirth.text.toString())
        } catch (e: ParseException) {
            Toast.makeText(context, "Invalid Date Format", Toast.LENGTH_SHORT).show()
            return
        }

        val _password = binding.editTextPassword.text.toString().trim()

        if(_password.isNotEmpty()) {
            val updatedProfile = ProfileModel(
                userId = binding.editTextId.text.toString(),
                email = binding.editTextEmail.text.toString(),
                functionality = binding.editTextFunctionality.text.toString(),
                name = binding.editTextName.text.toString(),
                dateOfBirth = _dateOfBirth,
                address = binding.editTextAddress.text.toString(),
                gender = binding.editTextGender.text.toString(),
                description = binding.editTextDescription.text.toString(),
                password = _password
                )
            updateProfile(updatedProfile)
        } else {
            val updatedProfile = ProfileModel(
                userId = binding.editTextId.text.toString(),
                email = binding.editTextEmail.text.toString(),
                functionality = binding.editTextFunctionality.text.toString(),
                name = binding.editTextName.text.toString(),
                dateOfBirth = _dateOfBirth,
                address = binding.editTextAddress.text.toString(),
                gender = binding.editTextGender.text.toString(),
                description = binding.editTextDescription.text.toString(),
                password = user!!.password
            )
            updateProfile(updatedProfile)
        }

    }

    private fun getUserProfile(): ProfileModel {
        // Implement the logic to retrieve the user profile
        // This might involve a database query or a network request
        val user=ProfileModel(
            userId = "abc",
            email = "abc",
            functionality = "USER",
            name = "abc",
            dateOfBirth = Date("2000-07-07"),
            address = "abc",
            gender = "MALE",
            description = "abc",
            password = "abc"
        )
        return user
    }

    private fun updateProfile(profile: ProfileModel) {
        // Implement the logic to update the user profile
        // This might involve a database update or a network request
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}