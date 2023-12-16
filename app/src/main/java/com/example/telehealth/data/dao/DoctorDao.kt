package com.example.telehealth.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.telehealth.data.dataclass.DoctorModel

@Dao
interface DoctorDao {

    @Insert
    fun insertDoctor(doctor: DoctorModel)

    @Query("SELECT * FROM doctors")
    fun getAllDoctors(): List<DoctorModel>

    @Query("SELECT * FROM doctors WHERE doctorId = :doctorId")
    fun getDoctorById(doctorId: String): DoctorModel

    // Additional queries for updating or deleting doctor records can be added here
}