package com.example.seepawandroid

import android.app.Application
import com.example.seepawandroid.data.providers.SessionManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initializations

        // Authentication tokens manager
        SessionManager.init(this)
    }
}