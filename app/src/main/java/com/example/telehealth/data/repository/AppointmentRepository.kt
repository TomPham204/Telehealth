package com.example.telehealth.data.repository

import android.content.Context
import com.example.telehealth.data.dataclass.AppointmentModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppointmentRepository(private val context: Context) {
    fun getAppointments(): MutableList<AppointmentModel> {
        val sharedPreferences = context.getSharedPreferences("Appointments", Context.MODE_PRIVATE)
        val appointmentsJson = sharedPreferences.getString("appointmentsKey", null)
        val appointments: MutableList<AppointmentModel> = mutableListOf()

        appointmentsJson?.let {
            val type = object : TypeToken<List<AppointmentModel>>() {}.type
            appointments.addAll(Gson().fromJson(it, type))
        }

        return appointments
    }

    fun setAppointments(appointments: List<AppointmentModel>) {
        val sharedPreferences = context.getSharedPreferences("Appointments", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(appointments)
        editor.putString("appointmentsKey", json)
        editor.apply()
    }
}
