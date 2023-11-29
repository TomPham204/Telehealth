package com.example.telehealth.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.telehealth.R
import com.example.telehealth.databinding.ProfileFragmentBinding

class ProfileFragment: Fragment() {
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: ProfileFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.profile_fragment, container, false
        )

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        // Observe profile data from ViewModel
        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            // Update UI with fetched profile data
            binding.textViewName.text = "Name: ${userProfile.userName}"
            binding.textViewEmail.text = "Email: ${userProfile.userEmail}"
            binding.textViewPhone.text = "Phone: ${userProfile.userPhoneNumber}"
        }

        // Fetch profile data (call this from ViewModel)
        viewModel.fetchUserProfile()

        return binding.root
    }
}