package com.example.seepawandroid

import android.app.Application
import com.example.seepawandroid.data.providers.SessionManager
import com.example.seepawandroid.utils.NetworkUtils

class SeePawApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        SessionManager.init(this)
        NetworkUtils.init(this)
    }
}
