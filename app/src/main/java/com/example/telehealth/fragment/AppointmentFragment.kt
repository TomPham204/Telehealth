package com.example.telehealth.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
import com.example.telehealth.VideoActivity
import com.example.telehealth.adapter.AppointmentAdapter
import com.example.telehealth.adapter.DoctorAdapter
import com.example.telehealth.adapter.DoctorSpinnerItem
import com.example.telehealth.adapter.OnRemoveClickListener
import com.example.telehealth.adapter.OnVideoCallClickListener
import com.example.telehealth.data.dataclass.AppointmentModel
import com.example.telehealth.data.dataclass.DoctorModel
import com.example.telehealth.viewmodel.AppointmentViewModel
import com.example.telehealth.viewmodel.DoctorViewModel
import com.example.telehealth.viewmodel.ProfileViewModel
import java.lang.Integer.max
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class AppointmentFragment : Fragment(), OnRemoveClickListener, OnVideoCallClickListener {

    private lateinit var appointmentsList: MutableList<AppointmentModel>
    private lateinit var adapter: AppointmentAdapter
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var doctorViewModel: DoctorViewModel
    private lateinit var profileViewModel: ProfileViewModel

    private var doctors = mutableListOf<DoctorModel>()
    private lateinit var doctorAdapter: DoctorAdapter

    // Datetime Control
    private val reserved_num_day_ahead: Int = 1
    private val num_days: Int = 3
    private val min_hour: Int = 8
    private val max_hour: Int = 17
    private val minute_interval: Int = 15

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // init the viewmodels
        val doctorFactory = DoctorViewModelFactory(requireContext())
        doctorViewModel = ViewModelProvider(this, doctorFactory)[DoctorViewModel::class.java]

        val appointmentFactory = AppointmentViewModelFactory(requireContext())
        appointmentViewModel = ViewModelProvider(this, appointmentFactory)[AppointmentViewModel::class.java]

        val profileFactory = ProfileViewModelFactory(requireContext())
        profileViewModel = ViewModelProvider(this, profileFactory)[ProfileViewModel::class.java]


        //inflate the fragment UI
        val view = inflater.inflate(R.layout.appointment_fragment, container, false)

        val doctorSpinner: Spinner = view.findViewById(R.id.doctorSpinner)
        val timeSpinner: Spinner = view.findViewById(R.id.timeSpinner)
        val saveButton: Button = view.findViewById(R.id.appointmentBtn)
        val recyclerView: RecyclerView = view.findViewById(R.id.appointmentsList)


        //get list of time choice for the dropdown
        val timeSlots = generateTimeSlots()
        val timeAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            timeSlots
        )
        timeSpinner.adapter = timeAdapter


        // get the list of doctor for the dropdown
        val doctorsDropdownList = doctors.map { doctor ->
            DoctorSpinnerItem("${doctor.doctorName}: ${doctor.specialty}", doctor)
        }
        doctorAdapter = DoctorAdapter(requireContext(), doctorsDropdownList)
        doctorSpinner.adapter = doctorAdapter

        observeDoctors()
        doctorViewModel.getAllDoctors()


        //get list of appointment to populate the list on UI
        adapter = AppointmentAdapter(mutableListOf<AppointmentModel>(), this as OnRemoveClickListener, this as OnVideoCallClickListener)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        observeAppointments()
        appointmentViewModel.getAllAppointments()
        recyclerView.adapter = adapter

        saveButton.setOnClickListener {
            saveAppointment()
        }

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

    private fun saveAppointment() {
        // Logic to get selected doctor and time from spinners
        val selectedDoctor = view?.findViewById<Spinner>(R.id.doctorSpinner)?.selectedItem!! as DoctorSpinnerItem
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val selectedTime = dateFormat.parse(view?.findViewById<Spinner>(R.id.timeSpinner)?.selectedItem.toString().trim())
        val id=UUID.randomUUID().toString()
        val userId=profileViewModel.getCurrentId()!!

        val newAppointment = AppointmentModel(id, userId, selectedDoctor.doctor.doctorId, selectedDoctor.doctor.doctorName, selectedTime, "PENDING")

        //write to local storage
        // Add new appointment to the existing list
        appointmentsList.add(newAppointment)

        // Save updated appointments list
        saveAppointment(newAppointment)

        // Update RecyclerView
        adapter.updateList(appointmentsList)

        // Update visibility of the appointment list
        updateUIVisibility()
    }

    private fun observeDoctors() {
        doctorViewModel.doctorsLiveData.observe(viewLifecycleOwner) { doctorsList ->
            doctors = doctorsList.toMutableList()

            val doctorsDropdownList = doctors.map { doctor ->
                DoctorSpinnerItem("${doctor.doctorName}: ${doctor.specialty}", doctor)
            }

            doctorAdapter.updateList(doctorsDropdownList)
        }
    }

    private fun saveAppointment(appointment: AppointmentModel) {
        appointmentViewModel.addOrUpdateAppointment(appointment)
    }

    private fun observeAppointments() {
        appointmentViewModel.appointmentsLiveData.observe(viewLifecycleOwner) {
            appointments ->
            appointmentsList=appointments.toMutableList()
            adapter.updateList(appointmentsList)
            updateUIVisibility()
        }
    }

    private fun generateTimeSlots(): ArrayList<String> {
        val timeSlots = ArrayList<String>()

        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        calendar.time = Date() // Set calendar to today


        // Resolve first day for compatibility
        if (this.reserved_num_day_ahead > 0) {
            calendar.add(Calendar.DAY_OF_MONTH, this.reserved_num_day_ahead)
        }
        val calendar_starthour = max(calendar.get(Calendar.HOUR), this.min_hour)
        val year_t = calendar.get(Calendar.YEAR)
        val month_t = calendar.get(Calendar.MONTH)
        val day_t = calendar.get(Calendar.DAY_OF_MONTH)
        for (h in calendar_starthour until this.max_hour) {
            for (m in 0 until 60 step this.minute_interval) {
                calendar.set(year_t, month_t, day_t, h, m)
                val timestamp = sdf.format(calendar.time)
                timeSlots.add(timestamp)
            }
        }
        calendar.add(Calendar.DAY_OF_MONTH, 1)

        // Generate timestamps from day 2 onward
        for (i in 1 until (this.num_days-1)) {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            for (hour in 8 until 17) {
                for (min in 0 until 60 step 15) {
                    calendar.set(year, month, day, hour, min)
                    val timestamp = sdf.format(calendar.time)
                    timeSlots.add(timestamp)
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1) // Move to the next day
        }

        return timeSlots
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
            intent.putExtra("DOCTOR_ID", appointment.doctorId)
            startActivity(intent)
        } catch(e: Exception) {
            Log.d("startVideoActivity", e.toString())
        }
    }
}
