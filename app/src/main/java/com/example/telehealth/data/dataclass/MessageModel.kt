package com.example.telehealth.data.dataclass

data class MessageModel(
    val text: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
