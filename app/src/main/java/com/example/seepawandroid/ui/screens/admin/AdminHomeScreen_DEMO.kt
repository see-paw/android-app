package com.example.seepawandroid.ui.screens.admin

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.data.providers.SessionManager
import com.example.seepawandroid.ui.screens.login.AuthViewModel

/**
 * Temporary home screen for authenticated Admins.
 */
@Composable
fun AdminHomeScreen_DEMO(authViewModel: AuthViewModel, sessionManager: SessionManager) {
    val userId = sessionManager.getUserId() ?: "Unknown"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Admin Panel",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Logged in as: AdminCAA")

        Spacer(modifier = Modifier.height(8.dp))

        Text("User ID: $userId", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            authViewModel.logout()
        }) {
            Text("Logout")
        }
    }
}