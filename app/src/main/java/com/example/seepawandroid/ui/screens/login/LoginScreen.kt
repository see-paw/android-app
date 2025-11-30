package com.example.seepawandroid.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Login screen composable.
 *
 * Provides UI for user authentication with email and password.
 * Observes LoginViewModel state and reacts to changes.
 *
 * @param viewModel ViewModel managing login logic and state
 * @param onLoginSuccess Callback invoked when login succeeds, receives userId and role
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit = {}
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.observeAsState(LoginUiState.Idle)
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")

    // Notify AuthViewModel when login succeeds
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            authViewModel.onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SeePaw Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("emailInput"),
            enabled = uiState !is LoginUiState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("passwordInput"),
            enabled = uiState !is LoginUiState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("loginButton"),
            enabled = uiState !is LoginUiState.Loading && email.isNotBlank() && password.isNotBlank()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is LoginUiState.Loading
        ) {
            Text("Criar conta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is LoginUiState.Loading -> {
                CircularProgressIndicator()
            }
            is LoginUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("errorMessage")
                )
            }
            else -> {}
        }
    }
}