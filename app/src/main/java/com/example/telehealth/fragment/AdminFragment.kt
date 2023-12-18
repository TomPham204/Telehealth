package com.example.telehealth.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.telehealth.data.dataclass.AppointmentModel
import com.example.telehealth.viewmodel.AppointmentViewModel
import com.example.telehealth.viewmodel.ProfileViewModel

class AdminFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var pendingAp: List<AppointmentModel>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        appointmentViewModel = ViewModelProvider(this).get(AppointmentViewModel::class.java)

    }

    private fun acceptAppointment(ap: AppointmentModel) {
        //call DB to update status
        appointmentViewModel.updateAppointment(ap.copy(status = "ACCEPTED"))
    }

    private fun rejectAppointment(ap: AppointmentModel) {
        //call DB to delete
        appointmentViewModel.deleteAppointmentsById(ap.appointmentId)
    }

    private fun deleteUser(uid: String) {
        //call DB to delete
        profileViewModel.deleteProfile(uid)
    }

    private fun getPendingAppointments() {
        pendingAp=appointmentViewModel.getAppointmentsByStatusAdmin("PENDING")
    }
}