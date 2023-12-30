package com.example.telehealth.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.telehealth.adapter.DoctorAdapter
import com.example.telehealth.adapter.DoctorSpinnerItem
import com.example.telehealth.adapter.MessageAdapter
import com.example.telehealth.data.dataclass.DoctorModel
import com.example.telehealth.data.dataclass.MessageModel
import com.example.telehealth.databinding.ChatFragmentBinding
import com.example.telehealth.viewmodel.ChatViewModel
import com.example.telehealth.viewmodel.DoctorViewModel
import com.example.telehealth.viewmodel.ProfileViewModel

class ChatFragment: Fragment() {
    private lateinit var adapter: MessageAdapter

    private var _binding: ChatFragmentBinding? = null
    private val binding get() = _binding!!
    private var currentUserId: String? = ""
    private var doctors = mutableListOf<DoctorModel>()
    private var messages = mutableListOf<MessageModel>()

    private lateinit var doctorViewModel: DoctorViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var doctorSpinnerAdapter: DoctorAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ChatFragmentBinding.inflate(inflater, container, false)


        // init the viewmodels
        val doctorFactory = DoctorViewModelFactory(requireContext())
        doctorViewModel = ViewModelProvider(this, doctorFactory)[DoctorViewModel::class.java]

        val profileFactory = ProfileViewModelFactory(requireContext())
        profileViewModel = ViewModelProvider(this, profileFactory)[ProfileViewModel::class.java]

        val chatFactory = ChatViewModelFactory(requireContext())
        chatViewModel = ViewModelProvider(this, chatFactory)[ChatViewModel::class.java]

        currentUserId = profileViewModel.getCurrentId()


        // init doctor dropdown
        val doctorsDropdownList = doctors.map { doctor ->
            DoctorSpinnerItem("${doctor.doctorName}: ${doctor.specialty}", doctor)
        }
        val doctorSpinner: Spinner = binding.chatDoctorSpinner
        doctorSpinnerAdapter = DoctorAdapter(requireContext(), doctorsDropdownList)
        observeDoctors()
        doctorViewModel.getAllDoctors()
        doctorSpinner.adapter = doctorSpinnerAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun setupRecyclerView() {
        observeChat()
        chatViewModel.getChatBySender(currentUserId!!)

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
            messages.add(message)
            adapter.updateMessages(messages)
            chatViewModel.addChat(message)
            binding.messageEditText.text.clear()
        }
    }

    private fun observeDoctors() {
        doctorViewModel.doctorsLiveData.observe(viewLifecycleOwner) { doctorsList ->
            doctors = doctorsList.toMutableList()

            // Update UI here
            val doctorsDropdownList = doctors.map { doctor ->
                DoctorSpinnerItem("${doctor.doctorName}: ${doctor.specialty}", doctor)
            }

            // Update the adapter's data and notify the adapter of the change
            doctorSpinnerAdapter.updateList(doctorsDropdownList)
        }
    }

    private fun observeChat() {
        chatViewModel.chatLiveData.observe(viewLifecycleOwner) {chats ->
            messages=chats.toMutableList()
            adapter.updateMessages(messages)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}

class ChatViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}