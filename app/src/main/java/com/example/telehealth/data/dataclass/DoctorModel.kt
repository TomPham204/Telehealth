package com.example.telehealth.data.dataclass

data class DoctorModel(
    // The doctorID is the same as the userID in the profile table
    val doctorId: String,
    val doctorName: String,
    val specialty: String,
) {
    constructor() : this("", "", "")
}