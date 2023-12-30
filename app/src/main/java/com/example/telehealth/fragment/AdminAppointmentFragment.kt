package com.example.telehealth.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
import com.example.telehealth.adapter.AppointmentAdapterAdmin
import com.example.telehealth.adapter.OnAcceptListener
import com.example.telehealth.adapter.OnRejectListener
import com.example.telehealth.data.dataclass.AppointmentModel
import com.example.telehealth.viewmodel.AppointmentViewModel

class AdminAppointmentFragment : Fragment(), OnRejectListener, OnAcceptListener {
    private lateinit var appointmentViewModel: AppointmentViewModel
    private var pendingAp: MutableList<AppointmentModel> = mutableListOf()
    private lateinit var appointmentsAdapter: AppointmentAdapterAdmin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.admin_appointment, container, false)


        //init viewmodel for reading appointments from db
        val appointmentFactory = AppointmentViewModelFactory(requireContext())
        appointmentViewModel = ViewModelProvider(this, appointmentFactory)[AppointmentViewModel::class.java]


        // now prepare the appointment list on UI for admin
        observeAps()
        appointmentViewModel.getPendingAppointments()

        val appointmentsList: RecyclerView = view.findViewById(R.id.appointmentsList)
        appointmentsAdapter = AppointmentAdapterAdmin(pendingAp, this as OnAcceptListener, this as OnRejectListener)
        appointmentsList.layoutManager = LinearLayoutManager(context)
        appointmentsList.adapter = appointmentsAdapter

        return view
    }

    override fun onAccept(ap: AppointmentModel) {
        pendingAp = pendingAp.map {i: AppointmentModel ->
            if(i.appointmentId != ap.appointmentId) {
                i
            } else {
                i.status="ACCEPTED"
                i
            }
        }.toMutableList()
        saveAppointments(ap)
        appointmentsAdapter.updateList(pendingAp)
    }

    override fun onReject(ap: AppointmentModel) {
        pendingAp= pendingAp.filter{ i: AppointmentModel -> i.appointmentId != ap.appointmentId}.toMutableList()
        ap.status="REJECTED"
        saveAppointments(ap)
        appointmentsAdapter.updateList(pendingAp)
    }

    private fun observeAps() {
        appointmentViewModel.pendingAppointmentsLiveData.observe(viewLifecycleOwner) { aps ->
            pendingAp = aps.toMutableList()
            appointmentsAdapter.updateList(pendingAp)
        }
    }

    private fun saveAppointments(ap: AppointmentModel) {
        appointmentViewModel.addOrUpdateAppointment(ap)
    }
}

class AppointmentViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}