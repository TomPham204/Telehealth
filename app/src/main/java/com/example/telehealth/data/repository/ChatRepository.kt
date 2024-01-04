package com.example.telehealth.data.repository

import android.content.Context
import android.util.Log
import com.example.telehealth.data.database.FirestoreDB
import com.example.telehealth.data.dataclass.MessageModel
import kotlinx.coroutines.tasks.await

class ChatRepository(private val context: Context) {
    // FirestoreDB has singleton, always return the same instance of db manager
    private val chatCollection = FirestoreDB.instance.collection("CHAT")

    suspend fun getAllChatBySenderId(senderId: String): List<MessageModel> {
        return try {
            chatCollection.whereEqualTo("senderId", senderId).get().await().mapNotNull { document ->
                document.toObject(MessageModel::class.java)
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error fetching messages", e)
            listOf()
        }
    }

    suspend fun getAllChatByReceiverId(receiverId: String): List<MessageModel> {
        return try {
            chatCollection.whereEqualTo("receiverId", receiverId).get().await().mapNotNull { document ->
                document.toObject(MessageModel::class.java)
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error fetching messages", e)
            listOf()
        }
    }

    suspend fun getAllChatBySenderAndReceiverId(senderId: String, receiverId: String): List<MessageModel> {
        return try {
            val firstQuery = chatCollection
                .whereEqualTo("senderId", senderId)
                .whereEqualTo("receiverId", receiverId)
                .get().await().mapNotNull { document ->
                    document.toObject(MessageModel::class.java)
                }

            val secondQuery = chatCollection
                .whereEqualTo("senderId", receiverId)
                .whereEqualTo("receiverId", senderId)
                .get().await().mapNotNull { document ->
                    document.toObject(MessageModel::class.java)
                }

            (firstQuery + secondQuery).sortedBy { it.timestamp }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error fetching messages", e)
            listOf()
        }
    }

    suspend fun addChat(msg: MessageModel) {
        chatCollection.add(msg)
    }
}
