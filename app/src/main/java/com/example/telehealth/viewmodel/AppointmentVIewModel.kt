package com.example.telehealth.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telehealth.data.dataclass.AppointmentModel
import com.example.telehealth.data.repository.AppointmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppointmentViewModel(context: Context) : ViewModel() {
    private val repository: AppointmentRepository = AppointmentRepository(context)
    private val _appointmentsLiveData = MutableLiveData<List<AppointmentModel>>()
    val appointmentsLiveData: LiveData<List<AppointmentModel>> = _appointmentsLiveData

    fun getAllAppointments() {
        viewModelScope.launch {
            val ap = withContext(Dispatchers.IO) {
                repository.getAppointments()
            }
            _appointmentsLiveData.postValue(ap)
        }
    }

    private val _pendingAppointmentsLiveData = MutableLiveData<List<AppointmentModel>>()
    val pendingAppointmentsLiveData: LiveData<List<AppointmentModel>> = _pendingAppointmentsLiveData

    // Function to fetch and post pending appointments
    fun getPendingAppointments() {
        viewModelScope.launch {
            val ap = withContext(Dispatchers.IO) {
                repository.getAppointments()
            }
            val pendingAp = ap.filter { it.status == "PENDING" }
            _pendingAppointmentsLiveData.postValue(pendingAp)
        }
    }

    fun addOrUpdateAppointment(appointment: AppointmentModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addOrUpdateAppointment(appointment)
            }
        }
    }

    fun deleteAppointment(apId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteAppointment(apId)
            }
        }
    }
}
