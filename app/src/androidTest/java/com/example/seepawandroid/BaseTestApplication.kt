package com.example.seepawandroid

import android.app.Application
import com.example.seepawandroid.utils.NetworkUtils

/**
 * Base application class for instrumented tests.
 *
 * Initializes NetworkUtils to match production behavior.
 * Hilt will generate a test application extending this class.
 */
open class BaseTestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        NetworkUtils.init(this)
    }
}