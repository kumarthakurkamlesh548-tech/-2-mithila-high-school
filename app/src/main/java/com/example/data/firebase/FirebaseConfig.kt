package com.example.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Reusable singleton providing access to Firebase Auth, Firestore, and Storage.
 * Configured with Firebase Project ID: "students-71ec1".
 */
object FirebaseConfig {
    val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e("FirebaseConfig", "FirebaseAuth error: ${e.message}")
            null
        }

    val db: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("FirebaseConfig", "FirebaseFirestore error: ${e.message}")
            null
        }

    val storage: FirebaseStorage?
        get() = try {
            FirebaseStorage.getInstance()
        } catch (e: Exception) {
            Log.e("FirebaseConfig", "FirebaseStorage error: ${e.message}")
            null
        }
}
