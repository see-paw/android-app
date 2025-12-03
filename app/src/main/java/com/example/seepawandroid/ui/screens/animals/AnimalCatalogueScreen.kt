package com.example.seepawandroid.ui.screens.animals

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R
import com.example.seepawandroid.ui.components.AnimalCard
import com.example.seepawandroid.ui.components.FilterBottomSheet
import com.example.seepawandroid.ui.components.PaginationBar
import com.example.seepawandroid.ui.screens.animals.AnimalCatalogueUiState
import com.example.seepawandroid.ui.screens.animals.viewmodel.AnimalViewModel

/**
 * Animal Catalogue Screen
 *
 * Displays a searchable, filterable and paginated list of animals.
 * The screen supports:
 *  - Search queries
 *  - Breed filters (via bottom sheet)
 *  - Multiple sorting options
 *  - Pagination controls
 *  - Loading, empty and error states
 *
 * This screen is intended to be used as the "home" of the public catalogue.
 *
 * Test Tags (for UI automation tests):
 *  - catalogueScreen
 *  - searchInput
 *  - filterButton
 *  - sortButton
 *  - loadingIndicator
 *  - emptyState
 *  - errorMessage
 *  - animalGrid
 *  - animalCard_<id>
 *  - paginationBar
 */
@Composable
fun AnimalCatalogueScreen(
    viewModel: AnimalViewModel,
    isLoggedIn: Boolean,
    onAnimalClick: (String) -> Unit
) {
    // Load animals on first composition
    LaunchedEffect(Unit) {
        viewModel.loadAnimals()
    }

    // Load favorites when user is logged in
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            viewModel.loadFavorites()
        }
    }

    // Observing ViewModel state
    val uiState by viewModel.uiState.observeAsState()
    val breedOptions by viewModel.breeds.observeAsState(emptyList())

    // UI state variables
    var searchText by remember { mutableStateOf(viewModel.currentSearchQuery) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    // Filter sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            breedOptions = breedOptions,
            onDismiss = { showFilterSheet = false },
            onApply = { filters ->
                viewModel.applyFilters(filters)
                showFilterSheet = false
            },
            onReset = {
                viewModel.applyFilters(null)
                showFilterSheet = false
            }
        )
    }

    // Root container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("catalogueScreen")
    ) {

        // Background wallpaper
        Image(
            painter = painterResource(id = R.drawable.seepaw_wallpaper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            /**
             * Search bar with filter and sort actions
             */
            OutlinedTextField(
                value = searchText,
                onValueChange = { newValue ->
                    searchText = newValue

                    // Prevent unnecessary repeated requests
                    if (newValue != viewModel.currentSearchQuery) {
                        viewModel.search(newValue)
                    }
                },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null)
                },
                trailingIcon = {
                    Row {
                        // Filter button
                        IconButton(
                            onClick = { showFilterSheet = true },
                            modifier = Modifier.testTag("filterButton")
                        ) {
                            Icon(Icons.Outlined.FilterList, contentDescription = null)
                        }

                        // Sort dropdown
                        Box {
                            IconButton(
                                onClick = { sortMenuExpanded = true },
                                modifier = Modifier.testTag("sortButton")
                            ) {
                                Icon(Icons.Outlined.Sort, contentDescription = null)
                            }

                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.sort_most_recent)) },
                                    modifier = Modifier.testTag("sort_recent_desc"),
                                    onClick = {
                                        viewModel.applySorting("created", "desc")
                                        sortMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.sort_oldest)) },
                                    modifier = Modifier.testTag("sort_oldest_asc"),
                                    onClick = {
                                        viewModel.applySorting("created", "asc")
                                        sortMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.sort_name_asc)) },
                                    modifier = Modifier.testTag("sort_name_asc"),
                                    onClick = {
                                        viewModel.applySorting("name", "asc")
                                        sortMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.sort_name_desc)) },
                                    modifier = Modifier.testTag("sort_name_desc"),
                                    onClick = {
                                        viewModel.applySorting("name", "desc")
                                        sortMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.sort_age_asc)) },
                                    modifier = Modifier.testTag("sort_age_asc"),
                                    onClick = {
                                        viewModel.applySorting("age", "asc")
                                        sortMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.sort_age_desc)) },
                                    modifier = Modifier.testTag("sort_age_desc"),
                                    onClick = {
                                        viewModel.applySorting("age", "desc")
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                },
                placeholder = { Text(stringResource(R.string.catalog_search_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("searchInput")
            )

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * State handler
             */
            when (val state = uiState) {

                null -> LoadingState()

                is AnimalCatalogueUiState.Loading -> LoadingState()

                is AnimalCatalogueUiState.Empty -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag("emptyState"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(
                                if (searchText.isNotBlank() || viewModel.currentFiltersNotEmpty())
                                    R.string.catalog_no_results
                                else
                                    R.string.catalog_empty
                            )
                        )

                    }
                }

                is AnimalCatalogueUiState.Error -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag("errorMessage"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.catalog_error, state.message))
                    }
                }

                /**
                 * Success state: render grid + pagination
                 */
                is AnimalCatalogueUiState.Success -> {
                    Column(Modifier.fillMaxSize()) {

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .testTag("animalGrid")
                        ) {
                            items(state.animals) { animal ->
                                AnimalCard(
                                    name = animal.name,
                                    age = animal.age,
                                    imageUrl = animal.imageUrl,
                                    isLoggedIn = isLoggedIn,
                                    isFavorite = state.favoriteIds.contains(animal.id),
                                    onClick = { onAnimalClick(animal.id) },
                                    onFavoriteClick = { viewModel.toggleFavorite(animal.id) },
                                    modifier = Modifier.testTag("animalCard_${animal.id}")
                                )
                            }
                        }

                        PaginationBar(
                            currentPage = state.currentPage,
                            totalPages = state.totalPages,
                            onPrev = { viewModel.previousPage() },
                            onNext = { viewModel.nextPage() },
                            onSelect = { page -> viewModel.goToPage(page) },
                            modifier = Modifier.testTag("paginationBar")
                        )
                    }
                }
            }
        }
    }
}

/**
 * Loading state displayed during API calls or initial data fetch.
 */
@Composable
private fun LoadingState() {
    Box(
        Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.testTag("loadingIndicator")
        )
    }
}
