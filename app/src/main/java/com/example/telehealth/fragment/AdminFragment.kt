package com.example.telehealth.fragment

import com.example.telehealth.data.dataclass.AppointmentModel

class AdminFragment {
    private fun acceptAppointment(ap: AppointmentModel) {
        //call DB to update status
        var _ap = ap
        _ap.status="ACCEPTED"

    }

    private fun rejectAppointment(id: String) {
        //call DB to delete
    }

    private fun deleteUser(uid: String) {
        //call DB to delete
    }

    private fun getPendingAppointments() {

    }
}