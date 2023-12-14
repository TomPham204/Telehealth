package com.example.telehealth.appointment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AppointmentFragment : Fragment(), OnRemoveClickListener, OnVideoCallClickListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appointmentsList: MutableList<Appointment>
    private lateinit var adapter: AppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        val doctorsList = listOf(
            Doctor("Dr. Smith", "Cardiology", "Hospital A",10),
            Doctor("Dr. Johnson", "Dermatology", "Clinic B", 11),
        )
        val doctorsDropdownList = doctorsList.map { doctor ->
            DoctorSpinnerItem("${doctor.name}: ${doctor.specialty}, ${doctor.workplace}", doctor)
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
        val selectedTime = view?.findViewById<Spinner>(R.id.timeSpinner)?.selectedItem.toString()

        // Add new appointment to the existing list
        val newAppointment = Appointment(selectedDoctor.doctor.name, selectedDoctor.doctor.id, convertToUnixTimestamp(selectedTime))
        appointmentsList.add(newAppointment)

        // Save updated appointments list to SharedPreferences
        saveAppointmentsToSharedPreferences(appointmentsList)

        // Update RecyclerView
        adapter.updateList(appointmentsList)

        // Update visibility of the appointment list
        updateUIVisibility()
    }

    private fun retrieveAppointments(): MutableList<Appointment> {
        val sharedPreferences = requireContext().getSharedPreferences("Appointments", Context.MODE_PRIVATE)
        val appointmentsJson = sharedPreferences.getString("appointmentsKey", null)
        val appointmentsList = mutableListOf<Appointment>()

        appointmentsJson?.let {
            val type = object : TypeToken<List<Appointment>>() {}.type
            appointmentsList.addAll(Gson().fromJson(it, type))
        }

        return appointmentsList
    }

    private fun saveAppointmentsToSharedPreferences(appointmentsList: List<Appointment>) {
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
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())

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

    private fun convertToUnixTimestamp(selectedTime: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
        val date = dateFormat.parse(selectedTime)
        return date?.time ?: 0L
    }

    override fun onRemoveClick(appointment: Appointment) {
        appointmentsList.remove(appointment)
        saveAppointmentsToSharedPreferences(appointmentsList)
        adapter.updateList(appointmentsList)
        updateUIVisibility()
    }

    override fun onVideoCallClick(appointment: Appointment) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/profile.php?id=${appointment.doctorId}"))
            startActivity(intent)
        } catch(e: Exception) {
            Log.d("startVideoActivity", e.toString())
        }
    }
}
