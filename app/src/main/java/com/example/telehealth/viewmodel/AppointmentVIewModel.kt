package com.example.telehealth.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.telehealth.data.dataclass.AppointmentModel
import com.example.telehealth.data.repository.AppointmentRepository

class AppointmentViewModel(context: Context) : ViewModel() {
    private val repository: AppointmentRepository = AppointmentRepository(context)

    fun getAllAppointments(): List<AppointmentModel> {
        return repository.getAppointments()
    }

    fun getPendingAppointments(): List<AppointmentModel> {
        return repository.getAppointments().filter { i: AppointmentModel -> i.status == "PENDING" }
    }

    fun setAppointments(appointments: List<AppointmentModel>) {
        return repository.setAppointments(appointments)
    }
}
