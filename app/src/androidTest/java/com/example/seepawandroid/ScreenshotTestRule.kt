package com.example.seepawandroid

import android.graphics.Bitmap
import android.os.Environment
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScreenshotTestRule : TestWatcher() {

    override fun failed(e: Throwable?, description: Description?) {
        super.failed(e, description)

        if (description == null) return

        try {
            val screenshot = Screenshot.capture()
            screenshot.format = Bitmap.CompressFormat.PNG
            screenshot.name = generateScreenshotName(description)

            screenshot.process()

            android.util.Log.i(
                "ScreenshotTestRule",
                "📸 Screenshot capturado: ${screenshot.name}"
            )

        } catch (ex: Exception) {
            android.util.Log.e(
                "ScreenshotTestRule",
                "❌ Falha ao capturar screenshot",
                ex
            )
        }
    }

    private fun generateScreenshotName(description: Description): String {
        val className = description.className.substringAfterLast('.')
        val methodName = description.methodName
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
            .format(Date())

        return "${className}_${methodName}_${timestamp}"
    }
}