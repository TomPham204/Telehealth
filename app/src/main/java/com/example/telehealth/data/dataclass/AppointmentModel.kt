package com.example.telehealth.data.dataclass

// https://developer.android.com/reference/androidx/room/package-summary
import java.util.Date

data class AppointmentModel(
    val appointmentId: String,
    val userId: String,
    val doctorId: String,
    val doctorName: String,
    val dateTime: Date,
    var status: String,
) {
    constructor() : this("", "", "", "", Date(), "",)
}