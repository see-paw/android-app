package com.example.seepawandroid.ui.screens.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.seepawandroid.R
import com.example.seepawandroid.ui.components.FavoriteAnimalCard

/**
 * Favorites Screen
 *
 * Displays a grid of the user's favorite animals.
 * The screen supports:
 *  - Grid layout of favorite animals
 *  - Loading, empty and error states
 *  - Remove favorite functionality
 *  - Navigation to animal detail page
 *
 * This screen is intended for authenticated users only.
 *
 * Test Tags (for UI automation tests):
 *  - favoritesScreen
 *  - loadingIndicator
 *  - emptyState
 *  - errorMessage
 *  - favoritesGrid
 *  - favoriteCard_<id>
 */
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onAnimalClick: (String) -> Unit
) {
    // Load favorites on first composition
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    // Observing ViewModel state
    val uiState by viewModel.uiState.observeAsState()

    // Root container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("favoritesScreen")
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

            // Title
            Text(
                text = stringResource(R.string.favorites_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // State handler
            when (val state = uiState) {
                null -> LoadingState()

                is FavoritesUiState.Loading -> LoadingState()

                is FavoritesUiState.Empty -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag("emptyState"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.favorites_empty),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                is FavoritesUiState.Error -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag("errorMessage"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.favorites_error, state.message),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is FavoritesUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("favoritesGrid")
                    ) {
                        items(state.favorites) { favorite ->
                            FavoriteAnimalCard(
                                name = favorite.name,
                                age = favorite.age,
                                imageUrl = favorite.principalImageUrl,
                                onClick = { onAnimalClick(favorite.id) },
                                onRemoveFavorite = { viewModel.removeFavorite(favorite.id) },
                                modifier = Modifier.testTag("favoriteCard_${favorite.id}")
                            )
                        }
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
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.testTag("loadingIndicator")
        )
    }
}
