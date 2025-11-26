package com.example.seepawandroid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.example.seepawandroid.data.managers.SessionManager
import com.example.seepawandroid.utils.NetworkUtils

/**
 * Custom Application class for the SeePaw app.
 *
 * This class enables:
 * - Global dependency injection through Hilt (@HiltAndroidApp).
 * - Initialization of application-wide services such as [NetworkUtils].
 *
 * It runs before any Activity or Service is created.
 */
@HiltAndroidApp
class SeePawApplication : Application() {

    /**
     * Called when the application is starting.
     * Initializes global utilities and services.
     */
    override fun onCreate() {
        super.onCreate()

        // Initialize network connectivity checker
        NetworkUtils.init(this)
    }
}
