package com.example.telehealth.data.dataclass

// https://developer.android.com/reference/androidx/room/package-summary
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "profiles")
data class ProfileModel(
    // Credential here
    @PrimaryKey val userId: String,
    val email: String,
    val password: String,
    val functionality: String,  // "USER", "DOCTOR", "ADMIN"

    // User's profile here
    val name: String,
    val address: String,
    val dateOfBirth: Date,
    val gender: String,         // "MALE" or "FEMALE"
    val description: String
) {
    constructor() : this("", "", "","","","",Date(),"","")
}