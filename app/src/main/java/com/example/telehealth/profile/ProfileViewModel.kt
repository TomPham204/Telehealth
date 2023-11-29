package com.example.telehealth.profile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    // Implement logic related to profile management here
    // You can have functions for updating user information, fetching data, etc.
    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile>
        get() = _userProfile

    // Fetch user profile data from repository
    fun fetchUserProfile() {
        // Simulated data retrieval - replace this with actual logic to fetch data
        val userId = "123" // Replace with actual user ID
        val fetchedUserProfile = repository.getUserProfile(userId)
        _userProfile.value = fetchedUserProfile
    }
}
