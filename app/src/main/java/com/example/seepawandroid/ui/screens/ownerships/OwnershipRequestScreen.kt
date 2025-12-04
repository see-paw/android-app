package com.example.seepawandroid.ui.screens.ownerships

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seepawandroid.R
import com.example.seepawandroid.ui.screens.ownership.OwnershipRequestViewModel

/**
 * Ownership Request Screen - Stateless Wizard Flow
 *
 * Manages a 3-step adoption request process:
 * 1. Terms & Conditions (with scroll tracking)
 * 2. Mock Payment Form (auto-filled)
 * 3. Success Confirmation
 *
 * All state is managed by OwnershipRequestViewModel.
 *
 * @param animalId The ID of the animal to adopt.
 * @param animalName Optional pre-loaded animal name (optimization).
 * @param shelterId Optional pre-loaded shelter ID (optimization).
 * @param onNavigateBack Callback to navigate back.
 * @param onRequestComplete Callback when ownership request is successfully submitted.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnershipRequestScreen(
    animalId: String,
    animalName: String?,
    shelterId: String?,
    onNavigateBack: () -> Unit,
    onRequestComplete: () -> Unit
) {
    val viewModel: OwnershipRequestViewModel = hiltViewModel()
    val uiState by viewModel.uiState.observeAsState(OwnershipRequestUiState.Loading)

    // Initialize on first composition
    LaunchedEffect(animalId) {
        viewModel.initialize(animalId, animalName, shelterId)
    }

    // Handle Android back button in form screen
    BackHandler(enabled = uiState is OwnershipRequestUiState.ShowingForm) {
        viewModel.goBackToTerms()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ownership_request_title)) },
                navigationIcon = {
                    // Only show back button in terms and form screens
                    if (uiState is OwnershipRequestUiState.ShowingTerms ||
                        uiState is OwnershipRequestUiState.ShowingForm
                    ) {
                        IconButton(
                            onClick = {
                                if (uiState is OwnershipRequestUiState.ShowingForm) {
                                    viewModel.goBackToTerms()
                                } else {
                                    onNavigateBack()
                                }
                            },
                            modifier = Modifier.testTag("backButton")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("ownershipRequestScreen")
        ) {
            when (val state = uiState) {
                is OwnershipRequestUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("loadingIndicator")
                    )
                }

                is OwnershipRequestUiState.ShowingTerms -> {
                    TermsContent(
                        animalName = state.animalName,
                        hasScrolledToBottom = state.hasScrolledToBottom,
                        onScrolledToBottom = { viewModel.onTermsScrolledToBottom() },
                        onAccept = { viewModel.acceptTerms() }
                    )
                }

                is OwnershipRequestUiState.ShowingForm -> {
                    FormContent(
                        animalName = state.animalName,
                        accountNumber = state.accountNumber,
                        holderName = state.holderName,
                        cvv = state.cvv,
                        citizenCard = state.citizenCard,
                        password = state.password,
                        isSubmitting = state.isSubmitting,
                        onSubmit = { viewModel.submitOwnershipRequest() }
                    )
                }

                is OwnershipRequestUiState.Success -> {
                    SuccessContent(
                        userName = state.userName,
                        animalName = state.animalName,
                        shelterName = state.shelterName,
                        onDone = onRequestComplete
                    )
                }

                is OwnershipRequestUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                            .testTag("errorState"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("backToAnimalButton")
                        ) {
                            Text(stringResource(R.string.back))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Stateless Terms & Conditions content.
 *
 * @param animalName Name of the animal being adopted.
 * @param hasScrolledToBottom Whether user has scrolled to the end.
 * @param onScrolledToBottom Callback when user reaches bottom.
 * @param onAccept Callback when user accepts terms.
 */
@Composable
private fun TermsContent(
    animalName: String,
    hasScrolledToBottom: Boolean,
    onScrolledToBottom: () -> Unit,
    onAccept: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Detect when user scrolls to bottom
    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        if (!hasScrolledToBottom && scrollState.value >= scrollState.maxValue - 50) {
            onScrolledToBottom()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("termsContent")
    ) {
        Text(
            text = stringResource(R.string.ownership_terms_title),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.ownership_terms_subtitle, animalName),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Scrollable terms
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .testTag("termsScrollArea")
        ) {
            Text(
                text = stringResource(R.string.ownership_terms_content),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accept button (enabled only after scroll)
        Button(
            onClick = onAccept,
            enabled = hasScrolledToBottom,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("acceptTermsButton")
        ) {
            Text(stringResource(R.string.accept_terms))
        }
    }
}

/**
 * Stateless Mock Form content with auto-filled data.
 *
 * @param animalName Name of the animal being adopted.
 * @param accountNumber Mock account number.
 * @param holderName Mock account holder name.
 * @param cvv Mock CVV.
 * @param citizenCard Mock citizen card number.
 * @param password Mock password (displayed as dots).
 * @param isSubmitting Whether request is being submitted.
 * @param onSubmit Callback to submit the request.
 */
@Composable
private fun FormContent(
    animalName: String,
    accountNumber: String,
    holderName: String,
    cvv: String,
    citizenCard: String,
    password: String,
    isSubmitting: Boolean,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("formContent")
    ) {
        Text(
            text = stringResource(R.string.ownership_form_title),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.ownership_form_subtitle, animalName),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Account Number
        OutlinedTextField(
            value = accountNumber,
            onValueChange = {},
            label = { Text(stringResource(R.string.account_number)) },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("accountNumberInput")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Holder Name
        OutlinedTextField(
            value = holderName,
            onValueChange = {},
            label = { Text(stringResource(R.string.holder_name)) },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("holderNameInput")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CVV
        OutlinedTextField(
            value = cvv,
            onValueChange = {},
            label = { Text(stringResource(R.string.cvv)) },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("cvvInput")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Citizen Card
        OutlinedTextField(
            value = citizenCard,
            onValueChange = {},
            label = { Text(stringResource(R.string.citizen_card)) },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("citizenCardInput")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = {},
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("passwordInput")
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Submit button
        Button(
            onClick = onSubmit,
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("submitRequestButton")
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(stringResource(R.string.submit_ownership_request))
        }
    }
}

/**
 * Stateless Success confirmation content.
 *
 * @param userName Name of the user who made the request.
 * @param animalName Name of the adopted animal.
 * @param shelterName Name of the shelter.
 * @param onDone Callback when user is done (closes the flow).
 */
@Composable
private fun SuccessContent(
    userName: String,
    animalName: String,
    shelterName: String,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("successContent"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.ownership_success_title, userName),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.ownership_success_message, animalName, shelterName),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.ownership_success_notification),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("doneButton")
        ) {
            Text(stringResource(R.string.done))
        }
    }
}