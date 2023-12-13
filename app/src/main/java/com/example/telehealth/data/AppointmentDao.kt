package com.example.telehealth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.telehealth.model.AppointmentModel

@Dao
interface AppointmentDao {

    @Insert
    fun insertAppointment(appointment: AppointmentModel)

    @Update
    fun updateAppointment(appointment: AppointmentModel)

    @Query("SELECT * FROM appointments WHERE userId = :userId")
    fun getAppointmentsByUser(userId: String): List<AppointmentModel>

    // You can add more queries as needed, such as delete or specific searches
}