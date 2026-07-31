package com.example.data.firebase

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Reusable singleton providing safe access to Firebase Auth, Firestore, and Realtime Database.
 * Configured with Firebase Project ID: "students-71ec1".
 */
object FirebaseConfig {

    private const val RTDB_URL = "https://students-71ec1-default-rtdb.firebaseio.com"

    val isFirebaseReady: Boolean
        get() = try {
            FirebaseApp.getApps(com.example.MithilaApplication.instance).isNotEmpty()
        } catch (e: Exception) {
            try {
                FirebaseApp.getInstance() != null
            } catch (ex: Exception) {
                false
            }
        }

    val auth: FirebaseAuth?
        get() = try {
            if (isFirebaseReady) {
                FirebaseAuth.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseConfig", "FirebaseAuth error: ${e.message}")
            null
        }

    val db: FirebaseFirestore?
        get() = try {
            if (isFirebaseReady) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseConfig", "FirebaseFirestore error: ${e.message}")
            null
        }

    val realtimeDb: FirebaseDatabase?
        get() = try {
            if (isFirebaseReady) {
                val app = FirebaseApp.getInstance()
                try {
                    FirebaseDatabase.getInstance(app, RTDB_URL)
                } catch (e: Exception) {
                    FirebaseDatabase.getInstance()
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseConfig", "FirebaseDatabase error: ${e.message}")
            null
        }
}

