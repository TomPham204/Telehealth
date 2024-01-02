package com.example.telehealth.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
import com.example.telehealth.VideoActivity
import com.example.telehealth.adapter.AppointmentAdapter
import com.example.telehealth.adapter.OnRemoveClickListener
import com.example.telehealth.adapter.OnVideoCallClickListener
import com.example.telehealth.data.dataclass.AppointmentModel
import com.example.telehealth.viewmodel.AppointmentViewModel
import com.example.telehealth.viewmodel.ProfileViewModel

class DoctorAppointmentFragment : Fragment(), OnRemoveClickListener, OnVideoCallClickListener {

    private lateinit var appointmentsList: MutableList<AppointmentModel>
    private lateinit var adapter: AppointmentAdapter
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var currentId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val appointmentFactory = AppointmentViewModelFactory(requireContext())
        appointmentViewModel = ViewModelProvider(this, appointmentFactory)[AppointmentViewModel::class.java]

        val profileFactory = ProfileViewModelFactory(requireContext())
        profileViewModel = ViewModelProvider(this, profileFactory)[ProfileViewModel::class.java]

        currentId = profileViewModel.getCurrentId()!!

        //inflate the fragment UI
        val view = inflater.inflate(R.layout.doctor_appointment, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.appointmentsList)


        //get list of appointment to populate the list on UI
        adapter = AppointmentAdapter(mutableListOf<AppointmentModel>(), this as OnRemoveClickListener, this as OnVideoCallClickListener)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        observeAppointments()
        appointmentViewModel.getAllAppointmentsOfDoctor(currentId)
        recyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun updateUIVisibility() {
        val recyclerView: RecyclerView = view?.findViewById(R.id.appointmentsList)!!
        val emptyTextView: TextView = view?.findViewById(R.id.emptyTextView)!!

        if (recyclerView != null && emptyTextView != null) {
            if (appointmentsList.isEmpty()) {
                emptyTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun observeAppointments() {
        appointmentViewModel.doctorAppointmentsLiveData.observe(viewLifecycleOwner) {
            appointments ->
            appointmentsList=appointments.toMutableList()
            adapter.updateList(appointmentsList)
            updateUIVisibility()
        }
    }

    override fun onRemoveClick(appointment: AppointmentModel) {
        appointmentsList.remove(appointment)
        appointmentViewModel.deleteAppointment(appointment.appointmentId)
        adapter.updateList(appointmentsList)
        updateUIVisibility()
    }

    override fun onVideoCallClick(appointment: AppointmentModel) {
        try {
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/profile.php?id=${appointment.doctorId}"))
            val intent = Intent(requireContext(), VideoActivity::class.java)
            intent.putExtra("CHANNEL", appointment.channel)
            startActivity(intent)
        } catch(e: Exception) {
            Log.d("startVideoActivity", e.toString())
        }
    }
}
