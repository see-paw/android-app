package com.example.seepawandroid.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seepawandroid.ui.navigation.AppScaffold
import com.example.seepawandroid.ui.screens.login.AuthViewModel

/**
 * Main entry point for the SeePaw application UI.
 *
 * This composable is called from MainActivity and sets up the entire app structure.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SeePawApp(authViewModel: AuthViewModel = hiltViewModel()) {
    AppScaffold(
        onLoginSuccess = {
            authViewModel.onLoginSuccess()
        },
        onLogout = {
            authViewModel.logout()
        }
    )
}