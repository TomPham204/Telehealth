package com.example.telehealth.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.data.repository.ProfileRepository

class ProfileViewModel(context: Context) : ViewModel() {
    private val repository: ProfileRepository = ProfileRepository(context)
    fun getProfileById(userId: String): ProfileModel? {
        return repository.getProfileById(userId)
    }

    fun getAllProfiles(): List<ProfileModel> {
        return repository.getProfiles()
    }

    fun setProfiles(profiles: List<ProfileModel>) {
        return repository.setProfiles(profiles)
    }

    fun getCurrentId(): String? {
        return repository.getCurrentId()
    }

    fun setCurrentId(id: String) {
        return repository.setCurrentId(id)
    }

    fun getCurrentProfile(): ProfileModel? {
        val savedId = getCurrentId() ?: return null
        val user = getAllProfiles().filter { i: ProfileModel -> i.userId == savedId }
        return if(user.isEmpty()) null else user[0]
    }
}
