package com.example.seepawandroid

import android.app.Application
import com.example.seepawandroid.data.providers.SessionManager
import com.example.seepawandroid.utils.NetworkUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SeePawApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        SessionManager.init(this)
        NetworkUtils.init(this)
    }
}
