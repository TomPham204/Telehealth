package com.example.telehealth.model

// https://developer.android.com/reference/androidx/room/package-summary
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileModel(
    @PrimaryKey val userId: String,
    val password: String,
    val name: String,
    val age: Int,
    val gender: String,
    val token: String,
)