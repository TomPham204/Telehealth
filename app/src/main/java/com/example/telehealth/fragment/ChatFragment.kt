package com.example.telehealth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.telehealth.adapter.DoctorAdapter
import com.example.telehealth.adapter.DoctorSpinnerItem
import com.example.telehealth.adapter.MessageAdapter
import com.example.telehealth.data.dataclass.DoctorModel
import com.example.telehealth.data.dataclass.MessageModel
import com.example.telehealth.databinding.ChatFragmentBinding
import com.example.telehealth.viewmodel.DoctorViewModel
import com.example.telehealth.viewmodel.ProfileViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatFragment: Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val messagesCollection = db.collection("messages")
    private lateinit var adapter: MessageAdapter

    private var _binding: ChatFragmentBinding? = null
    private val binding get() = _binding!!
    private var currentUserId: String? = ""
    private lateinit var doctorViewModel: DoctorViewModel
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ChatFragmentBinding.inflate(inflater, container, false)

        // init the viewmodels
        val doctorFactory = DoctorViewModelFactory(requireContext())
        doctorViewModel = ViewModelProvider(this, doctorFactory)[DoctorViewModel::class.java]

        val profileFactory = ProfileViewModelFactory(requireContext())
        profileViewModel = ViewModelProvider(this, profileFactory)[ProfileViewModel::class.java]

        currentUserId = profileViewModel.getCurrentId()

        // populate the doctor dropdown
        val doctorSpinner: Spinner = binding.chatDoctorSpinner
        val doctorsDropdownList = getDoctors().map { doctor ->
            DoctorSpinnerItem("${doctor.doctorName}: ${doctor.specialty}", doctor)
        }
        if(doctorsDropdownList.isEmpty()) {
            Toast.makeText(activity, "No doctor found, add doctor via admin", Toast.LENGTH_LONG).show()
        }
        val doctorAdapter = DoctorAdapter(requireContext(), doctorsDropdownList)
        doctorSpinner.adapter = doctorAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.sendButton.setOnClickListener {
            sendMessage()
        }
        listenForMessages()
    }

    private fun setupRecyclerView() {
        adapter = MessageAdapter(mutableListOf())
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.chatRecyclerView.adapter = adapter
    }

    private fun sendMessage() {
        val messageText = binding.messageEditText.text.toString().trim()
        val receiver = binding.chatDoctorSpinner.selectedItem.toString().trim()

        if(messageText.isEmpty() ||receiver.isEmpty()) {
            Toast.makeText(activity, "Message or receiver is empty", Toast.LENGTH_LONG).show()
            return
        }

        if (messageText.isNotEmpty()) {
            val message = MessageModel(text = messageText, senderId = currentUserId!!, receiverId = receiver)
            messagesCollection.add(message)
            binding.messageEditText.text.clear()
        }
    }

    private fun listenForMessages() {
        messagesCollection.orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                val messages = snapshot.toObjects(MessageModel::class.java)
                // Update adapter's data
                (adapter as? MessageAdapter)?.updateMessages(messages)
            }
    }

    private fun getDoctors(): List<DoctorModel> {
        return doctorViewModel.getAllDoctors()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
