package com.example.telehealth.model

// https://developer.android.com/reference/androidx/room/package-summary
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "appointments")
data class AppointmentModel(
    @PrimaryKey val appointmentId: String,
    val userId: String,
    val doctorId: String,
    val dateTime: Date,
    val description: String
    // Any other details relevant to an appointment
)