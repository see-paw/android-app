package com.example.seepawandroid.ui.screens.animals

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seepawandroid.R
import com.example.seepawandroid.data.models.enums.AnimalState
import com.example.seepawandroid.data.remote.dtos.animals.ResAnimalDto
import com.example.seepawandroid.ui.components.FosteringAmountDialog
import com.example.seepawandroid.ui.components.FosteringPaymentMockDialog
import com.example.seepawandroid.ui.components.FosteringProgressBar
import com.example.seepawandroid.ui.components.ImageCarousel
import com.example.seepawandroid.ui.navigation.NavigationRoutes

/**
 * Animal Detail Screen - Stateless
 *
 * Displays detailed information about a single animal.
 * All state is managed by AnimalDetailViewModel.
 *
 * Features:
 * - Image carousel
 * - Animal information (name, age, breed, description, etc.)
 * - Action buttons (Fostering - disabled, Ownership)
 * - Adopted animal card (when animal has owner)
 * - Online/Offline handling (buttons disabled when offline)
 * - Authentication check (login popup when not authenticated)
 * - Ownership verification (checks if user already has request)
 *
 * @param animalId The ID of the animal to display.
 * @param isAuthenticated Whether the user is logged in.
 * @param onNavigateBack Callback to navigate back.
 * @param onNavigateToLogin Callback to navigate to login screen.
 * @param onNavigateToOwnership Callback to navigate to ownership request with animalId.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailScreen(
    animalId: String,
    isAuthenticated: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToOwnership: (String) -> Unit
) {
    val viewModel: AnimalDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.observeAsState(AnimalDetailUiState.Loading)
    val showLoginDialog by viewModel.showLoginDialog.observeAsState(false)
    val showOwnershipExistsDialog by viewModel.showOwnershipExistsDialog.observeAsState()

    // Observe fostering
    val showFosteringDialog by viewModel.showFosteringDialog.observeAsState(false)
    val selectedFosteringAmount by viewModel.selectedFosteringAmount.observeAsState()
    val customFosteringAmount by viewModel.customFosteringAmount.observeAsState("")
    val fosteringAmountError by viewModel.fosteringAmountError.observeAsState(false)
    val fosteringResult by viewModel.fosteringResult.observeAsState()

    // Mock fostering payment
    val showPaymentMockDialog by viewModel.showPaymentMockDialog.observeAsState(false)
    val mockPaymentData by viewModel.mockPaymentData.observeAsState()

    // Load animal on first composition
    LaunchedEffect(animalId) {
        viewModel.loadAnimal(animalId)
    }

    // Login dialog
    if (showLoginDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLoginDialog() },
            title = { Text(stringResource(R.string.login_required)) },
            text = { Text(stringResource(R.string.login_required_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissLoginDialog()
                        onNavigateToLogin()
                    },
                    modifier = Modifier.testTag("loginDialogOkButton")
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissLoginDialog() }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
            modifier = Modifier.testTag("loginDialog")
        )
    }

    // Ownership exists dialog
    showOwnershipExistsDialog?.let { animalName ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissOwnershipExistsDialog() },
            title = { Text(stringResource(R.string.ownership_exists_title)) },
            text = { Text(stringResource(R.string.ownership_exists_message, animalName)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissOwnershipExistsDialog() },
                    modifier = Modifier.testTag("ownershipExistsDialogOkButton")
                ) {
                    Text("OK")
                }
            },
            modifier = Modifier.testTag("ownershipExistsDialog")
        )
    }

    // Fostering amount dialog
    if (showFosteringDialog) {
        FosteringAmountDialog(
            animalName = (uiState as? AnimalDetailUiState.Success)?.animal?.name ?: "",
            selectedAmount = selectedFosteringAmount,
            customAmount = customFosteringAmount,
            showError = fosteringAmountError,
            onDismiss = { viewModel.dismissFosteringDialog() },
            onAmountSelected = { amount -> viewModel.selectFosteringAmount(amount) },
            onCustomAmountChanged = { value -> viewModel.updateCustomFosteringAmount(value) },
            onConfirm = { viewModel.confirmFosteringAmount(animalId) }
        )
    }

    // Fostering payment mock dialog
    if (showPaymentMockDialog) {
        mockPaymentData?.let { paymentData ->
            FosteringPaymentMockDialog(
                animalName = (uiState as? AnimalDetailUiState.Success)?.animal?.name ?: "",
                amount = viewModel.pendingFosteringAmount ?: 0.0,
                paymentData = paymentData,
                onDismiss = { viewModel.dismissPaymentMockDialog() },
                onConfirm = { viewModel.confirmMockPayment() }
            )
        }
    }

    // Fostering result handling
    fosteringResult?.let { result ->
        AlertDialog(
            onDismissRequest = { viewModel.clearFosteringResult() },
            title = {
                Text(
                    if (result.isSuccess)
                        stringResource(R.string.fostering_success_title)
                    else
                        stringResource(R.string.fostering_error_title)
                )
            },
            text = {
                Text(
                    if (result.isSuccess)
                        stringResource(R.string.fostering_success_message)
                    else
                        result.exceptionOrNull()?.message ?: stringResource(R.string.fostering_error_message)
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearFosteringResult() },
                    modifier = Modifier.testTag("fosteringResultOkButton")
                ) {
                    Text("OK")
                }
            },
            modifier = Modifier.testTag(
                if (result.isSuccess) "fosteringSuccessDialog" else "fosteringErrorDialog"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.animal_details)) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("backButton")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("animalDetailScreen")
        ) {
            when (val state = uiState) {
                is AnimalDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("loadingIndicator")
                    )
                }

                is AnimalDetailUiState.Success -> {
                    AnimalDetailContent(
                        animal = state.animal,
                        animalId = animalId,
                        isOnline = true,
                        isAuthenticated = isAuthenticated,
                        canFoster = state.canFoster,
                        canRequestOwnership = state.canRequestOwnership,
                        onFosteringClick = {
                            if (isAuthenticated) {
                                viewModel.showFosteringDialog()
                            } else {
                                viewModel.showLoginDialog()
                            }
                        },
                        onOwnershipClick = {
                            if (isAuthenticated) {
                                // Check if user already has ownership request
                                if (viewModel.onOwnershipButtonClick(animalId, state.animal.name)) {
                                    // No existing request, navigate to wizard
                                    val route = buildString {
                                        append("${NavigationRoutes.OWNERSHIP_REQUEST_BASE}/${state.animal.id}")
                                        append("?animalName=${state.animal.name}")
                                        append("&shelterId=${state.animal.shelterId}")
                                    }
                                    onNavigateToOwnership(route)
                                }
                                // If returns false, dialog is already shown by ViewModel
                            } else {
                                viewModel.showLoginDialog()
                            }
                        }
                    )
                }

                is AnimalDetailUiState.SuccessOffline -> {
                    AnimalDetailContent(
                        animal = state.animal,
                        animalId = animalId,
                        isOnline = false,
                        isAuthenticated = isAuthenticated,
                        canFoster = state.canFoster,
                        canRequestOwnership = state.canRequestOwnership,
                        onFosteringClick = { /* Disabled */ },
                        onOwnershipClick = { /* Disabled */ }
                    )
                }

                is AnimalDetailUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                            .testTag("errorState"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (state.needsInternet) {
                                stringResource(R.string.needs_internet_for_details)
                            } else {
                                stringResource(R.string.error_loading_animal, state.message)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.retry(animalId) },
                            modifier = Modifier.testTag("retryButton")
                        ) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Stateless content display for animal details.
 *
 * @param animal The animal data to display.
 * @param animalId The ID of the animal.
 * @param isOnline Whether the device has internet connection.
 * @param isAuthenticated Whether the user is logged in.
 * @param canFoster Whether fostering action is available.
 * @param canRequestOwnership Whether ownership request action is available.
 * @param onFosteringClick Callback when fostering button is clicked.
 * @param onOwnershipClick Callback when ownership button is clicked.
 */
@Composable
private fun AnimalDetailContent(
    animal: ResAnimalDto,
    animalId: String,
    isOnline: Boolean,
    isAuthenticated: Boolean,
    canFoster: Boolean,
    canRequestOwnership: Boolean,
    onFosteringClick: () -> Unit,
    onOwnershipClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testTag("animalDetailContent")
    ) {
        // Image carousel
        val imageUrls = animal.images?.map { it.url } ?: emptyList()
        Log.d("AnimalDetail", "Image URLs: $imageUrls")
        ImageCarousel(
            imageUrls = imageUrls,
            animalName = animal.name
        )

        // Fostering progress bar
        FosteringProgressBar(
            currentSupportValue = animal.currentSupportValue,
            totalCost = animal.cost
        )

        // Animal information
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Name and age
            Text(
                text = "${animal.name}, ${animal.age} ${stringResource(R.string.animal_years_suffix)}",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.testTag("animalNameAge")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            animal.description?.let {
                Text(
                    text = stringResource(R.string.animal_description_label),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("animalDescription")
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Information section
            Text(
                text = stringResource(R.string.animal_info_label),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(label = stringResource(R.string.breed), value = animal.breed.name)
            InfoRow(label = stringResource(R.string.size), value = animal.size.name)
            InfoRow(label = stringResource(R.string.sex), value = animal.sex.name)
            InfoRow(label = stringResource(R.string.colour), value = animal.colour)
            InfoRow(
                label = stringResource(R.string.sterilized),
                value = if (animal.sterilized) stringResource(R.string.yes) else stringResource(R.string.no)
            )

            // Features
            animal.features?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.animal_features_label),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("animalFeatures")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Check if animal is adopted
            if (animal.animalState == AnimalState.HasOwner) {
                // Show cute adopted card instead of buttons
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)  // Light green
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("adoptedCard")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFF66BB6A),  // Green heart
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.animal_adopted, animal.name),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF2E7D32)  // Dark green
                        )
                    }
                }
            } else {
                // Show action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Calculate if animal is fully fostered
                    val isFullyFostered = animal.currentSupportValue >= animal.cost

                    Button(
                        onClick = onFosteringClick,
                        enabled = canFoster && !isFullyFostered && isOnline,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("fosteringButton")
                    ) {
                        Text(stringResource(R.string.fostering_button))
                    }

                    Button(
                        onClick = onOwnershipClick,
                        enabled = canRequestOwnership && isOnline,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ownershipButton")
                    ) {
                        Text(stringResource(R.string.ownership_button))
                    }
                }

                // Offline warning
                if (!isOnline) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.offline_actions_disabled),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.testTag("offlineWarning")
                    )
                }
            }
        }
    }
}

/**
 * Stateless info row component.
 *
 * @param label The label text.
 * @param value The value text.
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}