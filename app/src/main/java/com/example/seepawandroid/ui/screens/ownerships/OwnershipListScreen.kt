package com.example.seepawandroid.ui.screens.ownership

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.seepawandroid.R
import com.example.seepawandroid.data.models.enums.OwnershipStatus
import com.example.seepawandroid.data.remote.dtos.animals.ResOwnedAnimalDto
import com.example.seepawandroid.data.remote.dtos.ownerships.ResOwnershipRequestListDto
import com.example.seepawandroid.ui.components.OwnershipMockReceipts
import com.example.seepawandroid.utils.DateUtils

/**
 * Ownership List Screen - Displays user's ownership requests and owned animals.
 *
 * Features:
 * - Two tabs: Active Requests and Owned Animals
 * - List of ownership request cards with status badges
 * - List of owned animal cards
 * - Empty states per tab
 * - Click on card to view animal details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnershipListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCatalogue: () -> Unit,
    onNavigateToAnimal: (String) -> Unit
) {
    val viewModel: OwnershipListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.observeAsState(OwnershipListUiState.Loading)
    val selectedTabIndex by viewModel.selectedTabIndex.observeAsState(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ownership_list_title)) },
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
                .testTag("ownershipListScreen")
        ) {
            when (val state = uiState) {
                is OwnershipListUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("loadingIndicator")
                    )
                }

                is OwnershipListUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Tabs
                        TabRow(selectedTabIndex = selectedTabIndex) {
                            Tab(
                                selected = selectedTabIndex == 0,
                                onClick = { viewModel.onTabSelected(0) },
                                text = { Text(stringResource(R.string.ownership_active_requests)) }
                            )
                            Tab(
                                selected = selectedTabIndex == 1,
                                onClick = { viewModel.onTabSelected(1) },
                                text = { Text(stringResource(R.string.ownership_owned_animals)) }
                            )
                            // MOCK RECEIPTS
                            Tab(
                                selected = selectedTabIndex == 2,
                                onClick = { viewModel.onTabSelected(2) },
                                text = { Text("Faturas") }
                            )
                        }

                        // Content based on selected tab
//                        val itemsToShow = if (selectedTabIndex == 0) {
//                            state.activeRequests
//                        } else {
//                            state.ownedAnimals
//                        }
//
//                        if (itemsToShow.isEmpty()) {
//                            EmptyTabContent(
//                                isOwnedTab = selectedTabIndex == 1,
//                                onNavigateToCatalogue = onNavigateToCatalogue
//                            )
//                        } else {
//                            LazyColumn(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .testTag("ownershipListContent"),
//                                contentPadding = PaddingValues(16.dp),
//                                verticalArrangement = Arrangement.spacedBy(12.dp)
//                            ) {
//                                if (selectedTabIndex == 0) {
//                                    // Active requests tab
//                                    items(
//                                        items = state.activeRequests,
//                                        key = { it.id }
//                                    ) { request ->
//                                        OwnershipRequestCard(
//                                            request = request,
//                                            onClick = { onNavigateToAnimal(request.animalId) }
//                                        )
//                                    }
//                                } else {
//                                    // Owned animals tab
//                                    items(
//                                        items = state.ownedAnimals,
//                                        key = { it.id }
//                                    ) { owned ->
//                                        OwnedAnimalCard(
//                                            ownedAnimal = owned,
//                                            onClick = { onNavigateToAnimal(owned.animalId) }
//                                        )
//                                    }
//                                }
//                            }
//                        }
                        when (selectedTabIndex) {
                            0 -> {
                                if (state.activeRequests.isEmpty()) {
                                    EmptyTabContent(
                                        isOwnedTab = false,
                                        onNavigateToCatalogue = onNavigateToCatalogue
                                    )
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .testTag("ownershipListContent"),
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(items = state.activeRequests, key = { it.id }) { request ->
                                            OwnershipRequestCard(
                                                request = request,
                                                onClick = { onNavigateToAnimal(request.animalId) }
                                            )
                                        }
                                    }
                                }
                            }
                            1 -> {
                                if (state.ownedAnimals.isEmpty()) {
                                    EmptyTabContent(
                                        isOwnedTab = true,
                                        onNavigateToCatalogue = onNavigateToCatalogue
                                    )
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(items = state.ownedAnimals, key = { it.id }) { owned ->
                                            OwnedAnimalCard(
                                                ownedAnimal = owned,
                                                onClick = { onNavigateToAnimal(owned.animalId) }
                                            )
                                        }
                                    }
                                }
                            }
                            2 -> {
                                if (state.ownedAnimals.isEmpty()) {
                                    EmptyTabContent(
                                        isOwnedTab = true,
                                        onNavigateToCatalogue = onNavigateToCatalogue
                                    )
                                } else {
                                    OwnershipMockReceipts(ownedAnimals = state.ownedAnimals)
                                }
                            }
                        }
                    }
                }

                is OwnershipListUiState.Empty -> {
                    EmptyStateContent(
                        onNavigateToCatalogue = onNavigateToCatalogue
                    )
                }

                is OwnershipListUiState.Error -> {
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
                            onClick = { viewModel.retry() },
                            modifier = Modifier.testTag("retryButton")
                        ) {
                            Text(stringResource(R.string.ownership_list_retry))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card displaying an ownership request (active).
 *
 * @param request The ownership request data.
 * @param onClick Callback when card is clicked.
 */
@Composable
private fun OwnershipRequestCard(
    request: ResOwnershipRequestListDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("ownershipCard_${request.id}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = request.image.url,
                contentDescription = request.animalName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Ownership Request information
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = request.animalName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(
                        R.string.ownership_requested_at,
                        DateUtils.formatToPortugueseDate(request.requestedAt)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status badge
            StatusBadge(status = request.status)
        }
    }
}

/**
 * Card displaying an owned animal (approved ownership).
 *
 * @param ownedAnimal The owned animal data.
 * @param onClick Callback when card is clicked.
 */
@Composable
private fun OwnedAnimalCard(
    ownedAnimal: ResOwnedAnimalDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("ownedAnimalCard_${ownedAnimal.id}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Animal image
            if (ownedAnimal.image != null) {
                AsyncImage(
                    model = ownedAnimal.image.url,
                    contentDescription = ownedAnimal.animalName,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder when no image
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

            // Animal information
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = ownedAnimal.animalName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(
                        R.string.ownership_approved_at,
                        DateUtils.formatToPortugueseDate(ownedAnimal.approvedAt)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Approved badge (always green)
            StatusBadge(status = OwnershipStatus.Approved)
        }
    }
}

/**
 * Badge displaying ownership request status with color.
 *
 * @param status The ownership status.
 */
@Composable
private fun StatusBadge(status: OwnershipStatus) {
    val (backgroundColor, textColor, statusText) = when (status) {
        OwnershipStatus.Pending -> Triple(
            Color(0xFFFFF3E0),  // Light amber
            Color(0xFFEF6C00),  // Dark amber
            stringResource(R.string.ownership_status_pending)
        )
        OwnershipStatus.Analysing -> Triple(
            Color(0xFFE3F2FD),  // Light blue
            Color(0xFF1976D2),  // Dark blue
            stringResource(R.string.ownership_status_analysing)
        )
        OwnershipStatus.Approved -> Triple(
            Color(0xFFE8F5E9),  // Light green
            Color(0xFF388E3C),  // Dark green
            stringResource(R.string.ownership_status_approved)
        )
        OwnershipStatus.Rejected -> Triple(
            Color(0xFFFFEBEE),  // Light red
            Color(0xFFD32F2F),  // Dark red
            stringResource(R.string.ownership_status_rejected)
        )
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.testTag("statusBadge_${status.name}")
    ) {
        Text(
            text = statusText,
            color = textColor,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/**
 * Empty state for individual tabs.
 *
 * @param isOwnedTab Whether this is the owned animals tab.
 * @param onNavigateToCatalogue Callback to navigate to catalogue.
 */
@Composable
private fun EmptyTabContent(
    isOwnedTab: Boolean,
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
            text = if (isOwnedTab) {
                stringResource(R.string.ownership_no_owned_animals)
            } else {
                stringResource(R.string.ownership_no_active_requests)
            },
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToCatalogue,
            modifier = Modifier.testTag("goToCatalogueButton")
        ) {
            Text(stringResource(R.string.ownership_list_empty_action))
        }
    }
}

/**
 * Empty state when user has no ownership requests AND no owned animals.
 *
 * @param onNavigateToCatalogue Callback to navigate to catalogue.
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
            text = stringResource(R.string.ownership_list_empty_title),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.ownership_list_empty_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToCatalogue,
            modifier = Modifier.testTag("goToCatalogueButton")
        ) {
            Text(stringResource(R.string.ownership_list_empty_action))
        }
    }
}