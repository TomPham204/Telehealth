package com.example.telehealth.data.repository

import com.example.telehealth.data.dao.DoctorDao
import com.example.telehealth.data.dataclass.DoctorModel

class DoctorRepository(private val doctorDao: DoctorDao) {

    fun getAllDoctors(): List<DoctorModel> {
        return listOf(
            DoctorModel("abc","Dr. Smith", "Cardiology"),
            DoctorModel("xyz","Dr. Johnson", "Dermatology"),
        )
        return doctorDao.getAllDoctors()
    }

    fun getDoctorById(doctorId: String): DoctorModel {
        return doctorDao.getDoctorById(doctorId)
    }

    suspend fun insertDoctor(doctor: DoctorModel) {
        doctorDao.insertDoctor(doctor)
    }

    // Additional methods for updating or deleting doctors can be added here
}
