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

        // Install Uncaught Exception Handler to log startup crashes
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("MithilaApplication", "CRITICAL UNCAUGHT STARTUP CRASH in thread ${thread.name}: ${throwable.message}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("MithilaApplication", "FirebaseApp initialized successfully")
            }
            
            // Gracefully configure Realtime Database after FirebaseApp is ready
            try {
                val app = FirebaseApp.getInstance()
                if (app != null) {
                    val db = FirebaseDatabase.getInstance(app, "https://students-71ec1-default-rtdb.firebaseio.com")
                    try {
                        db.setPersistenceEnabled(true)
                        Log.d("MithilaApplication", "Firebase Realtime Database persistence configured")
                    } catch (pe: Throwable) {
                        Log.w("MithilaApplication", "Firebase Realtime Database persistence notice: ${pe.message}")
                    }
                }
            } catch (e: Throwable) {
                Log.w("MithilaApplication", "Firebase Realtime Database setup notice: ${e.message}")
            }
        } catch (e: Throwable) {
            Log.e("MithilaApplication", "Error initializing FirebaseApp: ${e.message}", e)
        }
    }
}
