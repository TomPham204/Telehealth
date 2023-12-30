package com.example.telehealth.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telehealth.data.dataclass.DoctorModel
import com.example.telehealth.data.repository.DoctorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DoctorViewModel(context: Context) : ViewModel() {
    private val repository: DoctorRepository = DoctorRepository(context)
    private val _doctorsLiveData = MutableLiveData<List<DoctorModel>>()
    val doctorsLiveData: LiveData<List<DoctorModel>> = _doctorsLiveData

    fun getAllDoctors() {
        viewModelScope.launch {
            val doctors = withContext(Dispatchers.IO) {
                repository.getDoctors()
            }
            _doctorsLiveData.postValue(doctors)
        }
    }

    fun getDoctorById(doctorId: String, onComplete: (DoctorModel?) -> Unit) {
        viewModelScope.launch {
            val doctor = withContext(Dispatchers.IO) {
                repository.getDoctorById(doctorId)
            }
            onComplete(doctor)
        }
    }

    fun setDoctors(doctors: List<DoctorModel>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.setDoctors(doctors)
            }
        }
    }

    fun addOrUpdateDoctor(doctor: DoctorModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addOrUpdateDoctor(doctor)
            }
        }
    }

    fun deleteDoctor(doctorId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteDoctor(doctorId)
            }
        }
    }
}
