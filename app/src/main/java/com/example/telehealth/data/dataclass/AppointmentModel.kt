package com.example.telehealth.data.dataclass

// https://developer.android.com/reference/androidx/room/package-summary
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "appointments")
data class AppointmentModel(
    @PrimaryKey val appointmentId: String,
    val userId: String,
    val doctorId: String,
    val doctorName: String,
    val dateTime: Date,
    var status: String,
)