package com.example.telehealth.data.repository

import android.content.Context
import com.example.telehealth.data.dataclass.DoctorModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DoctorRepository(private val context: Context) {
    fun getDoctors(): MutableList<DoctorModel> {
        val sharedPreferences = context.getSharedPreferences("Doctors", Context.MODE_PRIVATE)
        val doctorsJson = sharedPreferences.getString("doctorsKey", null)
        var doctors= mutableListOf<DoctorModel>()

        doctorsJson?.let {
            val type = object : TypeToken<List<DoctorModel>>() {}.type
            doctors.addAll(Gson().fromJson(it, type))
        }

        return doctors
    }

    fun getDoctorById(id: String): DoctorModel? {
        val doctors = getDoctors()
        val res = doctors.filter { i: DoctorModel -> i.doctorId == id}

        return if(res.isNotEmpty()) {
            res[0]
        } else {
            null
        }
    }

    fun setDoctors(doctors: List<DoctorModel>) {
        val sharedPreferences = context.getSharedPreferences("Doctors", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(doctors)
        editor.putString("doctorsKey", json)
        editor.apply()
    }
}
