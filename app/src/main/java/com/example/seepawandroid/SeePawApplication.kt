package com.example.seepawandroid

import android.app.Application
import com.example.seepawandroid.utils.NetworkUtils

class SeePawApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        NetworkUtils.init(this)
    }
}
