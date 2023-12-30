package com.example.telehealth.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.telehealth.data.dataclass.MessageModel
import com.example.telehealth.data.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(context: Context) : ViewModel() {
    private val repository: ChatRepository = ChatRepository(context)
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

    fun addChat(msg: MessageModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addChat(msg)
            }
        }
    }
}
