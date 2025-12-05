package com.example.seepawandroid.ui.screens.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seepawandroid.ui.components.DatePicker
import com.example.seepawandroid.utils.TestUtils

/**
 * Registration screen composable.
 *
 * Provides UI for new user registration with all required fields.
 * Observes RegisterViewModel state and reacts to changes.
 *
 * @param onNavigateBack Callback invoked when user wants to go back
 * @param onRegisterSuccess Callback invoked when registration succeeds
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    val viewModel: RegisterViewModel = hiltViewModel()

    val uiState by viewModel.uiState.observeAsState(RegisterUiState.Idle)
    val name by viewModel.name.observeAsState("")
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val confirmPassword by viewModel.confirmPassword.observeAsState("")
    val street by viewModel.street.observeAsState("")
    val city by viewModel.city.observeAsState("")
    val postalCode by viewModel.postalCode.observeAsState("")
    val birthDate by viewModel.birthDate.observeAsState(null)

    // Validation Errors
    val nameError by viewModel.nameError.observeAsState(null)
    val emailError by viewModel.emailError.observeAsState(null)
    val passwordError by viewModel.passwordError.observeAsState(null)
    val confirmPasswordError by viewModel.confirmPasswordError.observeAsState(null)
    val postalCodeError by viewModel.postalCodeError.observeAsState(null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Conta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("backButton")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Preencha os seus dados",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Nome Completo") },
                modifier = Modifier
                    .testTag("nameInput")
                    .fillMaxWidth()
                    .onFocusChanged { if (!it.isFocused) viewModel.validateName() },
                enabled = uiState !is RegisterUiState.Loading,
                singleLine = true,
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it) } }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                modifier = Modifier
                    .testTag("emailInput")
                    .fillMaxWidth()
                    .onFocusChanged { if (!it.isFocused) viewModel.validateEmail() },
                enabled = uiState !is RegisterUiState.Loading,
                singleLine = true,
                isError = emailError != null,
                supportingText = emailError?.let { { Text(it) } }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .testTag("passwordInput")
                    .fillMaxWidth()
                    .onFocusChanged { if (!it.isFocused) viewModel.validatePassword() },
                enabled = uiState !is RegisterUiState.Loading,
                singleLine = true,
                isError = passwordError != null,
                supportingText = {
                    Text(
                        text = passwordError ?: "Mínimo 8 caracteres, 1 maiúscula, 1 minúscula, 1 número, 1 especial (@#$%...)",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (passwordError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                label = { Text("Confirmar Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .testTag("confirmPasswordInput")
                    .fillMaxWidth()
                    .onFocusChanged { if (!it.isFocused) viewModel.validateConfirmPassword() },
                enabled = uiState !is RegisterUiState.Loading,
                singleLine = true,
                isError = confirmPasswordError != null,
                supportingText = confirmPasswordError?.let { { Text(it) } }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Birth Date field (with date picker component)
            DatePicker(
                value = birthDate,
                onDateSelected = { viewModel.onBirthDateChange(it) },
                label = "Data de Nascimento",
                modifier = Modifier
                    .testTag("birthDateInput")
                    .fillMaxWidth(),
                enabled = uiState !is RegisterUiState.Loading,
                supportingText = "Idade mínima: 18 anos",
                isTestMode = TestUtils.isInTestMode,
                testDateProvider = TestUtils.testDateProvider
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Street field
            OutlinedTextField(
                value = street,
                onValueChange = { viewModel.onStreetChange(it) },
                label = { Text("Morada") },
                modifier = Modifier
                    .testTag("streetInput")
                    .fillMaxWidth(),
                enabled = uiState !is RegisterUiState.Loading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // City and Postal Code in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { viewModel.onCityChange(it) },
                    label = { Text("Cidade") },
                    modifier = Modifier
                        .testTag("cityInput")
                        .weight(1f),
                    enabled = uiState !is RegisterUiState.Loading,
                    singleLine = true
                )

                OutlinedTextField(
                    value = postalCode,
                    onValueChange = { viewModel.onPostalCodeChange(it) },
                    label = { Text("Código Postal") },
                    modifier = Modifier
                        .testTag("postalCodeInput")
                        .weight(1f)
                        .onFocusChanged { if (!it.isFocused) viewModel.validatePostalCode() },
                    enabled = uiState !is RegisterUiState.Loading,
                    singleLine = true,
                    placeholder = { Text("0000-000") },
                    isError = postalCodeError != null,
                    supportingText = postalCodeError?.let { { Text(it) } }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register button
            Button(
                onClick = { viewModel.register() },
                modifier = Modifier
                    .testTag("registerButton")
                    .fillMaxWidth(),
                enabled = uiState !is RegisterUiState.Loading &&
                        name.isNotBlank() &&
                        email.isNotBlank() &&
                        password.isNotBlank() &&
                        confirmPassword.isNotBlank() &&
                        street.isNotBlank() &&
                        city.isNotBlank() &&
                        postalCode.isNotBlank() &&
                        birthDate != null
            ) {
                if (uiState is RegisterUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Text("Registar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            when (val state = uiState) {
                is RegisterUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("errorMessage")
                    )
                }
                else -> {}
            }
        }

        // Success Dialog
        if (uiState is RegisterUiState.Success) {
            AlertDialog(
                modifier = Modifier.testTag("successDialog"),
                onDismissRequest = { },
                title = { Text("Conta criada com sucesso!") },
                text = { Text("Bem-vindo(a) à SeePaw, ${name}!") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetUiState()
                            onRegisterSuccess()
                        },
                        modifier = Modifier.testTag("successDialogOkButton")
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}