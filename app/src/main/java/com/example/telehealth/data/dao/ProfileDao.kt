package com.example.telehealth.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.telehealth.data.dataclass.ProfileModel

@Dao
interface ProfileDao {

    @Insert
    fun insertProfile(profile: ProfileModel)

    @Query("SELECT * FROM profiles WHERE userId = :userId")
    fun getProfileById(userId: String): ProfileModel?

    @Query("SELECT token FROM profiles WHERE userId = :userId")
    fun getTokenById(userId: String): String?

    @Update
    suspend fun updateProfile(profile: ProfileModel)
}
