package com.example.telehealth.data.repository

import android.content.Context
import android.util.Log
import com.example.telehealth.data.database.FirestoreDB
import com.example.telehealth.data.dataclass.AppointmentModel
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class AppointmentRepository(private val context: Context) {
    // FirestoreDB has singleton, always return the same instance of db manager
    private val appointmentsCollection = FirestoreDB.instance.collection("APPOINTMENT")

    suspend fun getAppointments(): List<AppointmentModel> {
        return try {
            appointmentsCollection.get().await().map { document ->
                document.toObject()
            }
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Error fetching appointments", e)
            listOf()
        }
    }

    suspend fun addOrUpdateAppointment(ap: AppointmentModel) {
        ap.appointmentId?.let { id ->
            appointmentsCollection.document(id).set(ap).await()
        }
    }

    suspend fun deleteAppointment(apId: String) {
        appointmentsCollection.document(apId).delete().await()
    }
}
