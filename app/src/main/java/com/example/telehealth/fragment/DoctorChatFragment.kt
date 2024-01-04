package com.example.telehealth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.telehealth.adapter.MessageAdapter
import com.example.telehealth.adapter.MessageDisplayModel
import com.example.telehealth.adapter.PatientAdapter
import com.example.telehealth.adapter.PatientSpinnerItem
import com.example.telehealth.data.dataclass.MessageModel
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.databinding.ChatFragmentBinding
import com.example.telehealth.viewmodel.ChatViewModel
import com.example.telehealth.viewmodel.DoctorViewModel
import com.example.telehealth.viewmodel.ProfileViewModel

class DoctorChatFragment: Fragment() {
    private lateinit var adapter: MessageAdapter

    private var _binding: ChatFragmentBinding? = null
    private val binding get() = _binding!!
    private var currentUserId: String? = ""
    private var patients = mutableListOf<ProfileModel>()
    private var messages = mutableListOf<MessageDisplayModel>()

    private lateinit var doctorViewModel: DoctorViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var patientSpinnerAdapter: PatientAdapter

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init patient dropdown
        val patientsDropdownList = patients.map { p ->
            PatientSpinnerItem("${p.name}", p)
        }
        val patientSpinner: Spinner = binding.chatDoctorSpinner // reuse doctor spinner xml
        patientSpinnerAdapter = PatientAdapter(requireContext(), patientsDropdownList)
        observePatients()
        chatViewModel.loadPatientsForDoctor(currentUserId!!)
        patientSpinner.adapter = patientSpinnerAdapter
        binding.chatDoctorSpinner.setSelection(0)

        binding.chatDoctorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                chatViewModel.getChatBySenderAndReceiver(currentUserId!!, getCurrentSelected()!!.userId)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.chatDoctorSpinner.setSelection(0)
            }
        }

        setupRecyclerView()
        binding.sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun setupRecyclerView() {
        observeChat()

        adapter = MessageAdapter(mutableListOf())
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.chatRecyclerView.adapter = adapter
    }

    private fun getCurrentSelected(): ProfileModel? {
        val selected: PatientSpinnerItem = binding.chatDoctorSpinner.selectedItem as PatientSpinnerItem
        return if (selected is PatientSpinnerItem) {
            selected.patient
        } else {
            null
        }
    }

    private fun sendMessage() {
        val messageText = binding.messageEditText.text.toString().trim()
        val receiver = getCurrentSelected()!!.userId.toString().trim()

        if(messageText.isEmpty() ||receiver.isEmpty()) {
            Toast.makeText(activity, "Message or receiver is empty", Toast.LENGTH_LONG).show()
            return
        }

        if (messageText.isNotEmpty()) {
            val message = MessageModel(text = messageText, senderId = currentUserId!!, receiverId = receiver)
            messages.add(MessageDisplayModel("Me", message.text))
            adapter.updateMessages(messages)
            chatViewModel.addChat(message)
            binding.messageEditText.text.clear()
        }
    }

    private fun observePatients() {
        chatViewModel.patientListLiveData.observe(viewLifecycleOwner) { patientsList ->
            patients=patientsList.toMutableList()

            val patientsDropdownList = patients.map { p ->
                PatientSpinnerItem("${p.name}", p)
            }

            patientSpinnerAdapter.updateList(patientsDropdownList)
        }
    }

    private fun observeChat() {
        chatViewModel.chatLiveData.observe(viewLifecycleOwner) {chats ->
            messages = chats.map { chat ->
                val sender = if (currentUserId == chat.senderId) "Me" else "Other"
                MessageDisplayModel(sender = sender, text = chat.text)
            }.toMutableList()
            adapter.updateMessages(messages)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}