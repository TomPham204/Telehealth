package com.example.telehealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telehealth.data.dataclass.AppointmentModel
import com.example.telehealth.data.repository.AppointmentRepository
import kotlinx.coroutines.launch

class AppointmentViewModel(private val repository: AppointmentRepository) : ViewModel() {

    fun getAppointmentsByUser(userId: String): List<AppointmentModel> {
        return repository.getAppointmentsByUser(userId)
    }

    fun getAppointmentsByStatusAdmin(status: String): List<AppointmentModel> {
        return repository.getAppointmentsByStatusAdmin(status)
    }

    fun insertAppointment(appointment: AppointmentModel) {
        viewModelScope.launch {
            repository.insertAppointment(appointment)
        }
    }

    fun updateAppointment(appointment: AppointmentModel) {
        viewModelScope.launch {
            repository.updateAppointment(appointment)
        }
    }

    fun deleteAppointmentsById(id: String) {
        viewModelScope.launch {
            repository.deleteAppointmentsById(id)
        }
    }
}
