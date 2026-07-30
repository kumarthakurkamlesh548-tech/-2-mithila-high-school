package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class MithilaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("MithilaApplication", "FirebaseApp initialized successfully")
            }
        } catch (e: Exception) {
            Log.e("MithilaApplication", "Error initializing FirebaseApp: ${e.message}", e)
        }
    }
}
