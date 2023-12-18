package com.example.telehealth.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.telehealth.data.dataclass.AppointmentModel

@Dao
interface AppointmentDao {

    @Insert
    fun insertAppointment(appointment: AppointmentModel)

    @Update
    fun updateAppointment(appointment: AppointmentModel)

    @Query("SELECT * FROM appointments WHERE userId = :userId")
    fun getAppointmentsByUser(userId: String): List<AppointmentModel>

    @Query("DELETE FROM appointments WHERE appointmentId = :id")
    fun deleteAppointmentsById(id: String)

    @Query("SELECT * FROM appointments WHERE status = :status")
    fun getAppointmentsByStatusAdmin(status: String): List<AppointmentModel>
}