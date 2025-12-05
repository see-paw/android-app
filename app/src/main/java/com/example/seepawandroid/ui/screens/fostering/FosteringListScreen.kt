package com.example.seepawandroid.ui.screens.fosterings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seepawandroid.R
import com.example.seepawandroid.data.remote.dtos.fosterings.ResActiveFosteringDto
import com.example.seepawandroid.ui.components.FosteringMockReceipts
import com.example.seepawandroid.utils.DateUtils

/**
 * Fostering List Screen - Displays user's active fosterings and receipts.
 *
 * Features:
 * - Two tabs: Active Fosterings and Mock Receipts
 * - List of fostering cards with animal info and cancel button
 * - Click on card to view animal details
 * - Empty states per tab
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FosteringListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCatalogue: () -> Unit,
    onNavigateToAnimal: (String) -> Unit
) {
    val viewModel: FosteringListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.observeAsState(FosteringListUiState.Loading)
    val selectedTabIndex by viewModel.selectedTabIndex.observeAsState(0)
    val showCancelDialog by viewModel.showCancelDialog.observeAsState()

    // Cancel confirmation dialog
    showCancelDialog?.let { animalName ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissCancelDialog() },
            title = { Text(stringResource(R.string.fostering_cancel_title)) },
            text = { Text(stringResource(R.string.fostering_cancel_message, animalName)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.cancelFostering() },
                    modifier = Modifier.testTag("confirmCancelButton")
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissCancelDialog() },
                    modifier = Modifier.testTag("dismissCancelButton")
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
            modifier = Modifier.testTag("cancelFosteringDialog")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.fostering_list_title)) },
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
                .testTag("fosteringListScreen")
        ) {
            when (val state = uiState) {
                is FosteringListUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("loadingIndicator")
                    )
                }

                is FosteringListUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Tabs
                        TabRow(selectedTabIndex = selectedTabIndex) {
                            Tab(
                                selected = selectedTabIndex == 0,
                                onClick = { viewModel.onTabSelected(0) },
                                text = { Text(stringResource(R.string.fostering_active_tab)) },
                                modifier = Modifier.testTag("activeFosteringsTab")
                            )
                            Tab(
                                selected = selectedTabIndex == 1,
                                onClick = { viewModel.onTabSelected(1) },
                                text = { Text(stringResource(R.string.fostering_receipts_tab)) },
                                modifier = Modifier.testTag("receiptsTab")
                            )
                        }

                        // Content based on selected tab
                        when (selectedTabIndex) {
                            0 -> {
                                if (state.fosterings.isEmpty()) {
                                    EmptyTabContent(onNavigateToCatalogue = onNavigateToCatalogue)
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .testTag("fosteringListContent"),
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(
                                            items = state.fosterings,
                                            key = { it.animalName } // Using name as key since it's unique
                                        ) { fostering ->
                                            FosteringCard(
                                                fostering = fostering,
                                                onClick = {
                                                    val animalId = viewModel.getAnimalId(fostering.animalName)
                                                    if (animalId != null) {
                                                        onNavigateToAnimal(animalId)
                                                    }
                                                },
                                                onCancelClick = {
                                                    viewModel.showCancelDialog(fostering.animalName)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            1 -> {
                                // Receipts tab
                                FosteringMockReceipts(fosterings = state.fosterings)
                            }
                        }
                    }
                }

                is FosteringListUiState.Empty -> {
                    EmptyStateContent(onNavigateToCatalogue = onNavigateToCatalogue)
                }

                is FosteringListUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                            .testTag("errorState"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.retry() },
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
 * Card displaying a single active fostering.
 *
 * @param fostering The fostering data to display.
 * @param onClick Callback when card is clicked.
 * @param onCancelClick Callback when cancel button is clicked.
 */
@Composable
private fun FosteringCard(
    fostering: ResActiveFosteringDto,
    onClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("fosteringCard_${fostering.animalName}"),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Animal Image
                if (fostering.images?.isNotEmpty() == true) {
                    AsyncImage(
                        model = fostering.images.first().url,
                        contentDescription = fostering.animalName,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = fostering.animalName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.fostering_amount_monthly, fostering.amount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(
                            R.string.fostering_since,
                            DateUtils.formatToPortugueseDate(fostering.startDate)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cancelFosteringButton")
            ) {
                Text(stringResource(R.string.fostering_cancel_button))
            }
        }
    }
}

/**
 * Empty state for the active fosterings tab.
 */
@Composable
private fun EmptyTabContent(
    onNavigateToCatalogue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag("emptyTabState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Inbox,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.fostering_no_active),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToCatalogue,
            modifier = Modifier.testTag("goToCatalogueButton")
        ) {
            Text(stringResource(R.string.fostering_list_empty_action))
        }
    }
}

/**
 * Empty state when user has no fosterings at all.
 */
@Composable
private fun EmptyStateContent(
    onNavigateToCatalogue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag("emptyState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Inbox,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.fostering_list_empty_title),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.fostering_list_empty_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToCatalogue,
            modifier = Modifier.testTag("goToCatalogueButton")
        ) {
            Text(stringResource(R.string.fostering_list_empty_action))
        }
    }
}