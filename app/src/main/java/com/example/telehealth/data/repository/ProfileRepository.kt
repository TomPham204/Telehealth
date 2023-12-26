package com.example.telehealth.data.repository

import android.content.Context
import com.example.telehealth.data.dataclass.ProfileModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProfileRepository(private val context: Context) {

    fun getProfileById(userId: String): ProfileModel? {
        val user = getProfiles().filter { i: ProfileModel -> i.userId == userId }

        return if(user.isEmpty()) {
            null
        } else {
            user[0]
        }
    }

    fun getProfiles(): MutableList<ProfileModel> {
        val usersList = mutableListOf<ProfileModel>()
        val sharedPreferences = context.getSharedPreferences("Users", Context.MODE_PRIVATE)
        val usersJson = sharedPreferences.getString("usersKey", null)

        usersJson?.let {
            val type = object : TypeToken<List<ProfileModel>>() {}.type
            usersList.addAll(Gson().fromJson(it, type))
        }

        return usersList
    }

    fun setProfiles(users: List<ProfileModel>) {
        val sharedPreferences = context.getSharedPreferences("Users", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(users)
        editor.putString("usersKey", json)
        editor.apply()
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
