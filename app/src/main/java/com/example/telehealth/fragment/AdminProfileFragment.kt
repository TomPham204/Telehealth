package com.example.telehealth.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
import com.example.telehealth.adapter.DoctorAdapterAdmin
import com.example.telehealth.adapter.OnDeleteListener
import com.example.telehealth.data.dataclass.DoctorModel
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.databinding.AdminProfileBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AdminProfileFragment : Fragment(), OnDeleteListener {
//    private lateinit var profileViewModel: ProfileViewModel
//    private lateinit var doctorViewModel: DoctorViewModel
    private var doctors: MutableList<DoctorModel> = mutableListOf()
    private lateinit var doctorsAdapter: DoctorAdapterAdmin

    private var _binding: AdminProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.admin_profile, container, false)

        getDoctors()

        val doctorsList: RecyclerView = view.findViewById(R.id.doctorsList)
        doctorsAdapter = DoctorAdapterAdmin(doctors, this as OnDeleteListener)
        doctorsList.layoutManager = LinearLayoutManager(context)
        doctorsList.adapter = doctorsAdapter


//        profileViewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
//        appointmentViewModel = ViewModelProvider(requireActivity())[AppointmentViewModel::class.java]
//        doctorViewModel = ViewModelProvider(requireActivity())[DoctorViewModel::class.java]

        _binding = AdminProfileBinding.inflate(inflater, container, false)

        val addDoctorBtn: Button = _binding!!.addDoctorButtonAdmin
        addDoctorBtn.setOnClickListener {
            createDoctor()
        }
        Log.d("doctors 99", doctors.toString())

        return binding.root
    }

    override fun onDeleteDoctor(doctor: DoctorModel) {
        //call DB to delete
//        profileViewModel.deleteProfile(uid)

        doctors.filter { i: DoctorModel -> i.doctorId != doctor.doctorId }
        setDoctors(doctors)
        doctorsAdapter.updateList(doctors)
    }

    private fun getDoctors() {
//        doctors = doctorViewModel.getAllDoctors()
        val sharedPreferences = requireContext().getSharedPreferences("Doctors", Context.MODE_PRIVATE)
        val doctorsJson = sharedPreferences.getString("doctorsKey", null)

        doctorsJson?.let {
            val type = object : TypeToken<List<DoctorModel>>() {}.type
            doctors.addAll(Gson().fromJson(it, type))
        }
    }

    private fun setDoctors(doctors: List<DoctorModel>) {
        val sharedPreferences = requireContext().getSharedPreferences("Doctors", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(doctors)
        editor.putString("doctorsKey", json)
        editor.apply()
        Log.d("doctor save", "saved")
    }

    private fun getUsers(): MutableList<ProfileModel> {
        val usersList = mutableListOf<ProfileModel>()
        val sharedPreferences = requireContext().getSharedPreferences("Users", Context.MODE_PRIVATE)
        val usersJson = sharedPreferences.getString("usersKey", null)

        usersJson?.let {
            val type = object : TypeToken<List<ProfileModel>>() {}.type
            usersList.addAll(Gson().fromJson(it, type))
        }

        return usersList
    }

    private fun setUsers(users: List<ProfileModel>) {
        val sharedPreferences = requireContext().getSharedPreferences("Users", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(users)
        editor.putString("usersKey", json)
        editor.apply()
        Log.d("user save", "saved")
    }

    private fun createDoctor() {
        val id = UUID.randomUUID().toString()
        val email = binding.emailSignupText.text.toString()
        val password = binding.passwordSignupText.text.toString()
        val name = binding.nameSignupText.text.toString()
        val address = binding.addressSignupText.text.toString()
        val gender = binding.genderSignupText.selectedItem.toString()
        val specialty = binding.specialty.text.toString()
        val functionality = "DOCTOR"
        val description = ""

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var dateOfBirth: Date? = null
        try {
            dateOfBirth = dateFormat.parse(binding.dateOfBirthSignupText.text.toString().trim())
        } catch (e: ParseException) {
            Toast.makeText(context, "Invalid Date Format", Toast.LENGTH_SHORT).show()
            return
        }

        var currentUsers=getUsers()
        currentUsers.add(ProfileModel(id, email, password, functionality, name, address, dateOfBirth, gender, description))
        setUsers(currentUsers)

        Log.d("create user","create user")

        doctors.add(DoctorModel(id, name, specialty))
        setDoctors(doctors)
        doctorsAdapter.updateList(doctors)
        Log.d("create doctor","create doctor")
    }
}