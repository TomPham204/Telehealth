package com.example.telehealth.data.dataclass

// https://developer.android.com/reference/androidx/room/package-summary
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "doctors", foreignKeys = [
    ForeignKey(entity = ProfileModel::class, parentColumns = ["userId"],
        childColumns = ["doctorId"], onDelete = ForeignKey.CASCADE)
])
data class DoctorModel(
    // The doctorID is the same as the userID in the profile table
    // Me: Make the reference key here for the doctorID
    @PrimaryKey val doctorId: String,
    val doctorName: String,
    val specialty: String,
)