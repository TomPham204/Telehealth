package com.example.telehealth.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel(context: Context) : ViewModel() {
    private val repository: ProfileRepository = ProfileRepository(context)

    private val _currentProfile = MutableLiveData<ProfileModel?>()
    val currentProfile: LiveData<ProfileModel?> = _currentProfile

    private val _allProfiles = MutableLiveData<List<ProfileModel>>()
    val allProfiles: LiveData<List<ProfileModel>> = _allProfiles

    fun getProfileById(userId: String) {
        viewModelScope.launch {
            val profile = repository.getProfileById(userId)
            _currentProfile.postValue(profile)
        }
    }

    fun getAllProfiles() {
        viewModelScope.launch {
            val profiles = repository.getProfiles()
            _allProfiles.postValue(profiles)
        }
    }

    fun setProfiles(profiles: List<ProfileModel>) {
        viewModelScope.launch {
            repository.setProfiles(profiles)
        }
    }

    fun addOrUpdateProfile(profile: ProfileModel) {
        viewModelScope.launch {
            repository.addOrUpdateProfile(profile)
        }
    }

    fun getCurrentId(): String? {
        return repository.getCurrentId()
    }

    fun setCurrentId(id: String) {
        return repository.setCurrentId(id)
    }

    fun getCurrentProfile() {
        val savedId = getCurrentId()
        if(savedId!=null) {
            getProfileById(savedId)
        }
    }

    suspend fun userExistsWithEmail(email: String): Boolean {
        return repository.userExistsWithEmail(email)
    }
}
