package com.example.telehealth.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
import com.example.telehealth.adapter.AppointmentAdapterAdmin
import com.example.telehealth.adapter.OnAcceptListener
import com.example.telehealth.adapter.OnRejectListener
import com.example.telehealth.data.dataclass.AppointmentModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AdminAppointmentFragment : Fragment(), OnRejectListener, OnAcceptListener {
    //    private lateinit var profileViewModel: ProfileViewModel
//    private lateinit var appointmentViewModel: AppointmentViewModel

    private var pendingAp: MutableList<AppointmentModel> = mutableListOf()
    private lateinit var appointmentsAdapter: AppointmentAdapterAdmin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.admin_appointment, container, false)

        getPendingAppointments()
        val appointmentsList: RecyclerView = view.findViewById(R.id.appointmentsList)
        appointmentsAdapter = AppointmentAdapterAdmin(pendingAp, this as OnAcceptListener, this as OnRejectListener)
        appointmentsList.layoutManager = LinearLayoutManager(context)
        appointmentsList.adapter = appointmentsAdapter


//        profileViewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
//        appointmentViewModel = ViewModelProvider(requireActivity())[AppointmentViewModel::class.java]

        return view
    }

    override fun onAccept(ap: AppointmentModel) {
        //call DB to update status
//        appointmentViewModel.updateAppointment(ap.copy(status = "ACCEPTED"))
        for (i in pendingAp) {
            if(i.appointmentId == ap.appointmentId) {
                i.status="ACCEPTED"
            }
        }
        saveAppointments(pendingAp)
        appointmentsAdapter.updateList(pendingAp)
    }

    override fun onReject(ap: AppointmentModel) {
        //call DB to delete
//        appointmentViewModel.deleteAppointmentsById(ap.appointmentId)
        pendingAp= pendingAp.filter{ i: AppointmentModel -> i.appointmentId != ap.appointmentId}.toMutableList()
        saveAppointments(pendingAp)
        appointmentsAdapter.updateList(pendingAp)
    }

    private fun getPendingAppointments() {
//        pendingAp = appointmentViewModel.getAppointmentsByStatusAdmin("PENDING")
        val sharedPreferences = requireContext().getSharedPreferences("Appointments", Context.MODE_PRIVATE)
        val appointmentsJson = sharedPreferences.getString("appointmentsKey", null)

        appointmentsJson?.let {
            val type = object : TypeToken<List<AppointmentModel>>() {}.type

            pendingAp.addAll(Gson().fromJson(it, type))
        }
        Log.d("ap", pendingAp.toString())
    }

    private fun saveAppointments(newList: List<AppointmentModel>) {
        val sharedPreferences = requireContext().getSharedPreferences("Appointments", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(newList)
        editor.putString("appointmentsKey", json)
        editor.apply()
    }
}