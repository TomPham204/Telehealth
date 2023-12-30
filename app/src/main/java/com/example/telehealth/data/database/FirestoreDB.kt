package com.example.telehealth.data.database
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreDB {
    val instance: FirebaseFirestore by lazy {
        // Initialize Firebase Firestore
        FirebaseFirestore.getInstance()
    }
}