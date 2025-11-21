package com.example.seepawandroid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.example.seepawandroid.data.providers.SessionManager
import com.example.seepawandroid.utils.NetworkUtils

@HiltAndroidApp
class SeePawApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initializations
        NetworkUtils.init(this)
    }
}
