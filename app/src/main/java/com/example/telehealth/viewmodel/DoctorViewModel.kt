package com.example.telehealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telehealth.data.dataclass.DoctorModel
import com.example.telehealth.data.repository.DoctorRepository
import kotlinx.coroutines.launch

class DoctorViewModel(private val repository: DoctorRepository) : ViewModel() {

    fun getAllDoctors(): List<DoctorModel> {
        return repository.getAllDoctors()
    }

    fun getDoctorById(doctorId: String): DoctorModel {
        return repository.getDoctorById(doctorId)
    }

    fun insertDoctor(doctor: DoctorModel) {
        viewModelScope.launch {
            repository.insertDoctor(doctor)
        }
    }

    // Additional methods for updating or deleting doctors can be added here
}
