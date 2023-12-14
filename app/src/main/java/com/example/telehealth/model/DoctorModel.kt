package com.example.telehealth.model

// https://developer.android.com/reference/androidx/room/package-summary
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctors")
data class DoctorModel(
    @PrimaryKey val doctorId: String,
    val name: String,
    val specialty: String,
)