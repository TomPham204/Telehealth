package com.example.telehealth.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.telehealth.data.dataclass.DoctorModel
import com.example.telehealth.data.repository.DoctorRepository

class DoctorViewModel(context: Context) : ViewModel() {
    private val repository: DoctorRepository = DoctorRepository(context)

    fun getAllDoctors(): List<DoctorModel> {
        return repository.getDoctors()
    }

    fun getDoctorById(doctorId: String): DoctorModel? {
        return repository.getDoctorById(doctorId)
    }

    fun setDoctors(doctors: List<DoctorModel>) {
        return repository.setDoctors(doctors)
    }
}
