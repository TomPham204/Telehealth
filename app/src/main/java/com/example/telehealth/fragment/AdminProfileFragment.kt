package com.example.telehealth.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.telehealth.R
import com.example.telehealth.adapter.DoctorAdapterAdmin
import com.example.telehealth.adapter.OnDeleteListener
import com.example.telehealth.data.dataclass.DoctorModel
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.viewmodel.DoctorViewModel
import com.example.telehealth.viewmodel.ProfileViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AdminProfileFragment : Fragment(), OnDeleteListener {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var doctorViewModel: DoctorViewModel
    private var doctors: MutableList<DoctorModel> = mutableListOf()
    private lateinit var doctorsAdapter: DoctorAdapterAdmin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.admin_profile, container, false)


        //init viewmodels for reading db
        val doctorFactory = DoctorViewModelFactory(requireContext())
        doctorViewModel = ViewModelProvider(this, doctorFactory)[DoctorViewModel::class.java]

        val profileFactory = ProfileViewModelFactory(requireContext())
        profileViewModel = ViewModelProvider(this, profileFactory)[ProfileViewModel::class.java]


        // now prepare the doctors for admin screen
        //get all doctors using view model, then populate the doctor list on UI
        observeDoctors()
        doctorViewModel.getAllDoctors()

        val doctorsList: RecyclerView = view.findViewById(R.id.doctorsList)
        doctorsAdapter = DoctorAdapterAdmin(doctors, this as OnDeleteListener)
        doctorsList.layoutManager = LinearLayoutManager(context)
        doctorsList.adapter = doctorsAdapter

        //add listener to detect add doctor click
        val addDoctorBtn: Button = view.findViewById(R.id.addDoctorButtonAdmin)
        addDoctorBtn.setOnClickListener {
            createDoctor()
        }

        return view
    }

    override fun onDeleteDoctor(doctor: DoctorModel) {
        doctors = doctors.filter { i: DoctorModel -> i.doctorId != doctor.doctorId }.toMutableList()
        doctorViewModel.deleteDoctor(doctor.doctorId)
        doctorsAdapter.updateList(doctors)
    }

    private fun getUsers(): MutableList<ProfileModel> {
        return profileViewModel.getAllProfiles().toMutableList()
    }

    private fun setUsers(users: List<ProfileModel>) {
        profileViewModel.setProfiles(users)
    }

    private fun createDoctor() {
        val id = UUID.randomUUID().toString()
        val email = view?.findViewById<EditText>(R.id.emailSignupText)?.text.toString()
        val password = view?.findViewById<EditText>(R.id.passwordSignupText)?.text.toString()
        val name = view?.findViewById<EditText>(R.id.nameSignupText)?.text.toString()
        val address = view?.findViewById<EditText>(R.id.addressSignupText)?.text.toString()
        val gender = view?.findViewById<Spinner>(R.id.genderSignupText)?.selectedItem.toString()
        val specialty = view?.findViewById<EditText>(R.id.specialty)?.text.toString() // Make sure you have correct ID for specialty EditText
        val functionality = "DOCTOR"
        val description = ""

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var dateOfBirth: Date? = null
        try {
            dateOfBirth = dateFormat.parse(view?.findViewById<EditText>(R.id.dateOfBirthSignupText)?.text.toString().trim())
        } catch (e: ParseException) {
            Toast.makeText(context, "Invalid Date Format", Toast.LENGTH_SHORT).show()
            return
        }

        for (i in getUsers()) {
            if(i.email.toString().equals(email, true)) {
                Toast.makeText(context, "Email already used", Toast.LENGTH_LONG).show()
                return
            }
        }

        var currentUsers=getUsers()
        currentUsers.add(ProfileModel(id, email, password, functionality, name, address, dateOfBirth, gender, description))
        setUsers(currentUsers)

        val newDoctor = DoctorModel(id, name, specialty)
        doctors.add(newDoctor)
        doctorViewModel.addOrUpdateDoctor(newDoctor)
        doctorsAdapter.updateList(doctors)
    }

    private fun observeDoctors() {
        doctorViewModel.doctorsLiveData.observe(viewLifecycleOwner) { doctorsList ->
            doctors = doctorsList.toMutableList()
            doctorsAdapter.updateList(doctors)
        }
    }
}

class DoctorViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}