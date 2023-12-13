package com.example.telehealth.model

// https://developer.android.com/reference/androidx/room/package-summary
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserModel(
    @PrimaryKey val userId: String,
    val name: String,
    val email: String
    // Add other relevant user details
)