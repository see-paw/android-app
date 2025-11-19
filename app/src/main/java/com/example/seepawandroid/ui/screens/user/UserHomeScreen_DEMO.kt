package com.example.seepawandroid.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.data.providers.SessionManager
import com.example.seepawandroid.ui.screens.login.AuthViewModel

/**
* Temporary home screen for authenticated users.
*/
@Composable
fun UserHomeScreen_DEMO(authViewModel: AuthViewModel) {
    val userId = SessionManager.getUserId() ?: "Unknown"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to SeePaw!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Logged in as: User")

        Spacer(modifier = Modifier.height(8.dp))

        Text("User ID: $userId", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            authViewModel.logout()
        }) {
            Text("Clear Token and Logout")
        }
    }
}