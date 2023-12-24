package com.example.telehealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    fun getProfileById(userId: String): ProfileModel? {
        return repository.getProfileById(userId)
    }

    fun getTokenById(userId: String): String? {
        return repository.getTokenById(userId)
    }

    fun insertProfile(profile: ProfileModel) {
        viewModelScope.launch {
            repository.insertProfile(profile)
        }
    }

    fun updateProfile(profile: ProfileModel) {
        viewModelScope.launch {
            repository.updateProfile(profile)
        }
    }

    fun deleteProfile(userId: String) {
        viewModelScope.launch {
            repository.deleteProfile(userId)
        }
    }
}
