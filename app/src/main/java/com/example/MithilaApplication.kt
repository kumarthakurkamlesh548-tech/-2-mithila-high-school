package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MithilaApplication : Application() {

    companion object {
        lateinit var instance: MithilaApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("MithilaApplication", "FirebaseApp initialized successfully")
            }
            
            // Gracefully configure Realtime Database after FirebaseApp is ready
            try {
                if (FirebaseApp.getApps(this).isNotEmpty()) {
                    val db = FirebaseDatabase.getInstance("https://students-71ec1-default-rtdb.firebaseio.com")
                    db.setPersistenceEnabled(true)
                    Log.d("MithilaApplication", "Firebase Realtime Database configured with persistence")
                }
            } catch (e: Exception) {
                Log.w("MithilaApplication", "Firebase Realtime Database optional persistence setup notice: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e("MithilaApplication", "Error initializing FirebaseApp: ${e.message}", e)
        }
    }
}
