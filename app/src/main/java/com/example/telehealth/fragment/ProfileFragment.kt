package com.example.telehealth.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.telehealth.MainActivity
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.databinding.ProfileFragmentBinding
import com.example.telehealth.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private var user: ProfileModel? = null
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        val profileFactory = ViewModelFactory(requireContext())
        profileViewModel = ViewModelProvider(this, profileFactory)[ProfileViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileData()

        binding.saveButton.setOnClickListener {
            saveProfileData()
        }

        binding.logoutButton.setOnClickListener {
                logout()
        }
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("USER_ID", null)
            apply()
        }
        (activity as? MainActivity)?.replaceFragment(LoginFragment())
    }

    private fun loadProfileData() {
        val userProfile = getUserProfile()!!

        binding.editTextId.text = userProfile.userId
        binding.editTextEmail.setText(userProfile.email)
        binding.editTextFunctionality.setText(userProfile.functionality)
        binding.editTextName.setText(userProfile.name)
        binding.editTextDateOfBirth.text= userProfile.dateOfBirth.toString()
        binding.editTextAddress.setText(userProfile.address)
        binding.editTextGender.setText(userProfile.gender)
        binding.editTextDescription.setText(userProfile.description)
        // Don't set the password for security reasons
    }

    private fun saveProfileData() {
        // Save the updated profile data
        val _password = binding.editTextPassword.text.toString().trim()

        if(_password.isNotEmpty()) {
            val updatedProfile = ProfileModel(
                userId = binding.editTextId.text.toString(),
                email = binding.editTextEmail.text.toString(),
                functionality = binding.editTextFunctionality.text.toString(),
                name = binding.editTextName.text.toString(),
                dateOfBirth = user!!.dateOfBirth,
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
                dateOfBirth = user!!.dateOfBirth,
                address = binding.editTextAddress.text.toString(),
                gender = binding.editTextGender.text.toString(),
                description = binding.editTextDescription.text.toString(),
                password = user!!.password
            )
            updateProfile(updatedProfile)
        }
        Toast.makeText(activity, "Profile updated", Toast.LENGTH_LONG).show()
    }

    private fun getUserProfile(): ProfileModel? {
        user = profileViewModel.getCurrentProfile()
        return user
    }

    private fun updateProfile(profile: ProfileModel) {
        val usersList = getUsers().map { user ->
            if (user.userId == profile.userId) profile else user
        }
        saveUsers(usersList)
    }

    private fun getUsers(): MutableList<ProfileModel> {
        val usersList = profileViewModel.getAllProfiles().toMutableList()
        return usersList
    }

    private fun saveUsers(users: List<ProfileModel>) {
        profileViewModel.setProfiles(users)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}