package com.example.telehealth.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.telehealth.MainActivity
import com.example.telehealth.R
import com.example.telehealth.data.LocalDatabase
import com.example.telehealth.databinding.LoginScreenBinding
import com.example.telehealth.databinding.ProfileFragmentBinding
import com.example.telehealth.model.ProfileModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment() {
    private lateinit var binding: ViewBinding
    private lateinit var callbackManager: CallbackManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        callbackManager = CallbackManager.Factory.create()
        val existedId=checkLoginStatus()
        return if (existedId != "") {
            val profileBinding: ProfileFragmentBinding = DataBindingUtil.inflate(
                inflater, R.layout.profile_fragment, container, false
            )

            displayProfileInfo(profileBinding, existedId)

            profileBinding.logoutButton.setOnClickListener {
                saveLoginStatus("")
                remountProfileFragment()
            }

            profileBinding.saveButton.setOnClickListener {
                updateProfile(profileBinding, existedId)
            }

            profileBinding.root
        } else {
            binding = LoginScreenBinding.inflate(inflater, container, false)
            showLoginForm()
            binding.root
        }
    }

    private fun checkLoginStatus(): String {
        return try {
            val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
            sharedPreferences.getString("USER_ID", "").toString()
        } catch(error: Exception) {
            Log.d("checkLoginStatus", error.toString())
            ""
        }
    }

    private fun saveLoginStatus(currentUserId: String) {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("USER_ID", currentUserId)
            apply()
        }
    }

    private fun showLoginForm() {
        val loginBinding = binding as? LoginScreenBinding
        loginBinding?.facebookLoginButton?.setPermissions("public_profile")
        loginBinding?.facebookLoginButton?.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val token = loginResult.accessToken.token

                val request = GraphRequest.newMeRequest(
                    loginResult.accessToken
                ) { jsonObject, response ->
                    if (response?.error != null) {
                        // Handle errors, if any.
                        Log.e("fb_oauth", response.error.toString())
                    } else if (jsonObject != null) {
                        val userId = jsonObject.getString("id")
                        val userName = jsonObject.getString("name")

                        checkUserInDatabase(userId, userName, token)
                    }
                }

                var parameters = Bundle()
                parameters.putString("fields", "id,name")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                Log.d("fb_error", exception.toString())
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
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

    private fun updateProfile(binding: ProfileFragmentBinding, existedId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val profileDao = LocalDatabase.getDatabase(requireContext()).profileDao()
            val name = binding.textViewName.text.toString()
            val age = binding.textViewAge.text.toString().toIntOrNull() ?: 0 // Defaulting to 0 if conversion fails
            val gender = binding.textViewGender.text.toString()
            val token = profileDao.getTokenById(existedId)!!
            val password = binding.textViewPassword.text.toString()

            val updatedProfile = ProfileModel(existedId, password, name ,age, gender, token)

            profileDao.updateProfile(updatedProfile)

            withContext(Dispatchers.Main) {
                // Show a confirmation message or do any other UI related actions
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayProfileInfo(binding: ProfileFragmentBinding, existedId: String) {
        lifecycleScope.launch {
            val profileDao = LocalDatabase.getDatabase(requireContext()).profileDao()
            val userProfile = profileDao.getProfileById(existedId)

            // Now update the UI with the retrieved profile information
            // Make sure this is done on the main thread
            userProfile?.let { profile ->
                withContext(Dispatchers.Main) {
                    binding.textViewId.text = profile.userId
                    binding.textViewName.setText(profile.name)
                    binding.textViewAge.setText(profile.age.toString())
                    binding.textViewGender.setText(profile.gender)
                }
            }
        }
    }

    private fun checkUserInDatabase(userId: String, userName: String, token: String) {
        try {
            // Use Room's coroutine support to move database operations off the main thread
            lifecycleScope.launch {
                val profileDao = LocalDatabase.getDatabase(requireContext()).profileDao()
                val existingUser = profileDao.getProfileById(userId)

                if (existingUser == null) {
                    // User does not exist, so let's insert them into the database
                    val newUser = ProfileModel(userId, "user", userName, 0, "male", token)
                    profileDao.insertProfile(newUser)
                }

                // Save the login status with the current user ID
                saveLoginStatus(userId)
                remountProfileFragment()
            }
        } catch (error: Exception) {
            Log.e("checkUserInDatabase", error.toString())
            remountProfileFragment()
        }
    }

}