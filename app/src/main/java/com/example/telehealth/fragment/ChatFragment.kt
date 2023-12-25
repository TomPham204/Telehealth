package com.example.telehealth.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.telehealth.adapter.DoctorAdapter
import com.example.telehealth.adapter.DoctorSpinnerItem
import com.example.telehealth.adapter.MessageAdapter
import com.example.telehealth.data.dataclass.DoctorModel
import com.example.telehealth.data.dataclass.MessageModel
import com.example.telehealth.databinding.ChatFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChatFragment: Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val messagesCollection = db.collection("messages")
    private lateinit var adapter: MessageAdapter // Replace with your custom adapter

    private var _binding: ChatFragmentBinding? = null
    private val binding get() = _binding!!
    private var currentUserId: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ChatFragmentBinding.inflate(inflater, container, false)

        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getString("USER_ID", null)

        val doctorSpinner: Spinner = binding.chatDoctorSpinner
        val doctorsDropdownList = getDoctors().map { doctor ->
            DoctorSpinnerItem("${doctor.doctorName}: ${doctor.specialty}", doctor)
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
        val messageText = binding.messageEditText.text.toString()
        val receiver = binding.chatDoctorSpinner.selectedItem.toString()

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

    private fun getDoctors(): MutableList<DoctorModel> {
        val sharedPreferences = requireContext().getSharedPreferences("Doctors", Context.MODE_PRIVATE)
        val doctorsJson = sharedPreferences.getString("doctorsKey", null)
        val doctors: MutableList<DoctorModel> = mutableListOf()

        doctorsJson?.let {
            val type = object : TypeToken<List<DoctorModel>>() {}.type
            doctors.addAll(Gson().fromJson(it, type))
        }
        return doctors

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
