package com.example.seepawandroid

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.seepawandroid.ui.SeePawApp
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point Activity for the SeePaw Android application.
 *
 * This activity:
 * - Is annotated with @AndroidEntryPoint to enable Hilt dependency injection.
 * - Sets the Jetpack Compose UI tree using [SeePawApp].
 * - Requires Android API level 26 (Oreo) or above due to application constraints.
 */
@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created.
     * Sets up the Jetpack Compose UI content for the entire app.
     *
     * @param savedInstanceState Saved state from previous configurations.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeePawApp()
        }
    }
}
