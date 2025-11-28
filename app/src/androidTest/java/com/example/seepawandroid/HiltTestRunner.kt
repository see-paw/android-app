package com.example.seepawandroid

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Custom test runner for Hilt instrumentation tests.
 *
 * Replaces the application with HiltTestApplication to allow
 * dependency injection in tests.
 */
//class HiltTestRunner : AndroidJUnitRunner() {
//
//    override fun newApplication(
//        cl: ClassLoader?,
//        className: String?,
//        context: Context?
//    ): Application {
//        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
//    }
//}
class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, CustomTestApplication_Application::class.java.name, context)
    }
}