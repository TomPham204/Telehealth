package com.example.telehealth.data.repository

import android.content.Context
import android.util.Log
import com.example.telehealth.data.database.FirestoreDB
import com.example.telehealth.data.dataclass.ProfileModel
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class ProfileRepository(private val context: Context) {
    private val profileCollection = FirestoreDB.instance.collection("PROFILE")

    suspend fun getProfileById(userId: String): ProfileModel? {
        return try {
            val snapshot = profileCollection.document(userId).get().await()
            snapshot.toObject<ProfileModel>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getProfiles(): List<ProfileModel> {
        return try {
            profileCollection.get().await().mapNotNull { document ->
                document.toObject<ProfileModel>()
            }
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error fetching profiles", e)
            listOf()
        }
    }

    suspend fun setProfiles(users: List<ProfileModel>) {
        users.forEach { user ->
            user.userId?.let { id ->
                profileCollection.document(id).set(user).await()
            }
        }
    }

    suspend fun addOrUpdateProfile(user: ProfileModel) {
        user.userId?.let { id ->
            profileCollection.document(id).set(user).await()
        }
    }

    suspend fun userExistsWithEmail(email: String): Boolean {
        return try {
            val querySnapshot = profileCollection.whereEqualTo("email", email).limit(1).get().await()
            querySnapshot.documents.isNotEmpty()
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            false
        }
    }

    fun getCurrentId(): String? {
        val sharedPreferences = context.getSharedPreferences("USER_ID", Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("currentId", null)
        return id
    }

    fun setCurrentId(id: String) {
        val sharedPreferences = context.getSharedPreferences("USER_ID", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("currentId", id.toString())
        editor.apply()
    }
}
