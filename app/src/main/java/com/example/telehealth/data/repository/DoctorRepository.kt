package com.example.telehealth.data.repository

import android.content.Context
import android.util.Log
import com.example.telehealth.data.database.FirestoreDB
import com.example.telehealth.data.dataclass.DoctorModel
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class DoctorRepository(private val context: Context) {
    // FirestoreDB has singleton, always return the same instance of db manager
    private val doctorsCollection = FirestoreDB.instance.collection("DOCTOR")

    suspend fun getDoctors(): List<DoctorModel> {
        return try {
            doctorsCollection.get().await().map { document ->
                document.toObject()
            }
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Error fetching doctors", e)
            listOf()
        }
    }

    suspend fun getDoctorById(id: String): DoctorModel? {
        return try {
            doctorsCollection.document(id).get().await().toObject<DoctorModel>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun setDoctors(doctors: List<DoctorModel>) {
        // In Firestore, each doctor needs to be added individually
        doctors.forEach { doctor ->
            doctor.doctorId?.let { id ->
                doctorsCollection.document(id).set(doctor).await()
            }
        }
    }

    suspend fun addOrUpdateDoctor(doctor: DoctorModel) {
        doctor.doctorId?.let { id ->
            doctorsCollection.document(id).set(doctor).await()
        }
    }

    suspend fun deleteDoctor(doctorId: String) {
        doctorsCollection.document(doctorId).delete().await()
    }
}
