package com.example.telehealth.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.telehealth.viewmodel.AppointmentViewModel
import com.example.telehealth.viewmodel.DoctorViewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class AppointmentFragment : Fragment(), OnRemoveClickListener, OnVideoCallClickListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appointmentsList: MutableList<AppointmentModel>
    private lateinit var adapter: AppointmentAdapter

    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var doctorViewModel: DoctorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        appointmentViewModel = ViewModelProvider(requireActivity())[AppointmentViewModel::class.java]
        doctorViewModel = ViewModelProvider(requireActivity())[DoctorViewModel::class.java]

        val view = inflater.inflate(R.layout.appointment_fragment, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("appointments", Context.MODE_PRIVATE)
        appointmentsList = retrieveAppointments()

        val doctorSpinner: Spinner = view.findViewById(R.id.doctorSpinner)
        val timeSpinner: Spinner = view.findViewById(R.id.timeSpinner)
        val saveButton: Button = view.findViewById(R.id.appointmentBtn)
        val recyclerView: RecyclerView = view.findViewById(R.id.appointmentsList)

        val timeSlots = generateTimeSlots()
        val timeAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            timeSlots
        )
        timeSpinner.adapter = timeAdapter

        val doctorsList = doctorViewModel.getAllDoctors()

        val doctorsDropdownList = doctorsList.map { doctor ->
            DoctorSpinnerItem("${doctor.doctorName}: ${doctor.specialty}", doctor)
        }
        val doctorAdapter = DoctorAdapter(requireContext(), doctorsDropdownList)
        doctorSpinner.adapter = doctorAdapter

        // Set up RecyclerView
        adapter = AppointmentAdapter(appointmentsList, this as OnRemoveClickListener, this as OnVideoCallClickListener)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        saveButton.setOnClickListener {
            saveAppointment()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUIVisibility()
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
        val userId=checkLoginStatus()

        // write to DB
        val newAppointment = AppointmentModel(id, userId, selectedDoctor.doctor.doctorId, selectedDoctor.doctor.doctorName, selectedTime, "PENDING")
        appointmentViewModel.insertAppointment(newAppointment)

        //write to local storage
        // Add new appointment to the existing list
        appointmentsList.add(newAppointment)

        // Save updated appointments list to SharedPreferences
//        saveListAppointments(appointmentsList)

        // Update RecyclerView
        adapter.updateList(appointmentsList)

        // Update visibility of the appointment list
        updateUIVisibility()
    }

    private fun retrieveAppointments(): MutableList<AppointmentModel> {
        val appointmentsList = mutableListOf<AppointmentModel>()
        // get from db
        val data = appointmentViewModel.getAppointmentsByUser(checkLoginStatus())
        appointmentsList.addAll(data)

        // get from local storage
//        val sharedPreferences = requireContext().getSharedPreferences("Appointments", Context.MODE_PRIVATE)
//        val appointmentsJson = sharedPreferences.getString("appointmentsKey", null)

//        appointmentsJson?.let {
//            val type = object : TypeToken<List<AppointmentModel>>() {}.type
//            appointmentsList.addAll(Gson().fromJson(it, type))
//        }

        return appointmentsList
    }

    private fun saveListAppointments(appointmentsList: List<AppointmentModel>) {
        // save to local storage
        val sharedPreferences = requireContext().getSharedPreferences("Appointments", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(appointmentsList)
        editor.putString("appointmentsKey", json)
        editor.apply()
    }

    private fun generateTimeSlots(): ArrayList<String> {
        val timeSlots = ArrayList<String>()

        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        calendar.time = Date() // Set calendar to today

        // Generate timestamps for tomorrow and the day after tomorrow
        for (i in 0 until 2) {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            for (hour in 8 until 17) {
                for (min in 0 until 60 step 5) {
                    calendar.set(year, month, day, hour, min)
                    val timestamp = sdf.format(calendar.time)
                    timeSlots.add(timestamp)
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1) // Move to the next day
        }

        return timeSlots
    }

    private fun checkLoginStatus(): String {
        return try {
            val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
            val id=sharedPreferences.getString("USER_ID", "")
            id!!
        } catch(error: Exception) {
            Log.d("checkLoginStatus", error.toString())
            ""
        }
    }

    override fun onRemoveClick(appointment: AppointmentModel) {
        appointmentsList.remove(appointment)
//        saveListAppointments(appointmentsList)
        appointmentViewModel.deleteAppointmentsById(appointment.appointmentId)
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
