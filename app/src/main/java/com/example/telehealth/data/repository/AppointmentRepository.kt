package com.example.telehealth.data.repository

import com.example.telehealth.data.dao.AppointmentDao
import com.example.telehealth.data.dataclass.AppointmentModel

class AppointmentRepository(private val appointmentDao: AppointmentDao) {

    fun getAppointmentsByUser(userId: String): List<AppointmentModel> {
        return appointmentDao.getAppointmentsByUser(userId)
    }

    fun getAppointmentsByStatusAdmin(status: String): List<AppointmentModel> {
        return appointmentDao.getAppointmentsByStatusAdmin(status)
    }

    suspend fun insertAppointment(appointment: AppointmentModel) {
        appointmentDao.insertAppointment(appointment)
    }

    suspend fun updateAppointment(appointment: AppointmentModel) {
        appointmentDao.updateAppointment(appointment)
    }

    suspend fun deleteAppointmentsById(id: String) {
        appointmentDao.deleteAppointmentsById(id)
    }
}
