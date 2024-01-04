package com.example.telehealth.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telehealth.data.dataclass.MessageModel
import com.example.telehealth.data.dataclass.ProfileModel
import com.example.telehealth.data.repository.ChatRepository
import com.example.telehealth.data.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(context: Context) : ViewModel() {
    private val repository: ChatRepository = ChatRepository(context)
    private val profileRepository: ProfileRepository = ProfileRepository(context)
    private val _chatLiveData = MutableLiveData<List<MessageModel>>()
    val chatLiveData: LiveData<List<MessageModel>> = _chatLiveData

    fun getChatBySender(id: String) {
        viewModelScope.launch {
            val chat = withContext(Dispatchers.IO) {
                repository.getAllChatBySenderId(id)
            }
            _chatLiveData.postValue(chat)
        }
    }

    fun getChatByReceiver(id: String) {
        viewModelScope.launch {
            val chat = withContext(Dispatchers.IO) {
                repository.getAllChatByReceiverId(id)
            }
            _chatLiveData.postValue(chat)
        }
    }

    fun getChatBySenderAndReceiver(senderId: String, receiverId: String) {
        viewModelScope.launch {
            val chat = withContext(Dispatchers.IO) {
                repository.getAllChatBySenderAndReceiverId(senderId, receiverId)
            }
            _chatLiveData.postValue(chat)
        }
    }

    fun addChat(msg: MessageModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addChat(msg)
            }
        }
    }

    private val _patientListLiveData = MutableLiveData<List<ProfileModel>>()
    val patientListLiveData: LiveData<List<ProfileModel>> = _patientListLiveData

    fun loadPatientsForDoctor(doctorId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val messages = repository.getAllChatByReceiverId(doctorId)
            val uniqueSenderIds = messages.map { it.senderId }.toSet()

            val senders = uniqueSenderIds.mapNotNull { senderId ->
                profileRepository.getProfileById(senderId)
            }

            withContext(Dispatchers.Main) {
                _patientListLiveData.postValue(senders)
            }
        }
    }
}
