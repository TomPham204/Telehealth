package com.example.telehealth.data.repository

import com.example.telehealth.data.dao.ProfileDao
import com.example.telehealth.data.dataclass.ProfileModel

class ProfileRepository(private val profileDao: ProfileDao) {

    fun getProfileById(userId: String): ProfileModel? {
        return profileDao.getProfileById(userId)
    }

    fun getTokenById(userId: String): String? {
        return profileDao.getTokenById(userId)
    }

    suspend fun insertProfile(profile: ProfileModel) {
        profileDao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: ProfileModel) {
        profileDao.updateProfile(profile)
    }

    suspend fun deleteProfile(userId: String) {
        profileDao.deleteProfile(userId)
    }
}
