package com.example.seepawandroid.ui.screens.animals

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import com.example.seepawandroid.R
import com.example.seepawandroid.ui.components.AnimalCard
import com.example.seepawandroid.ui.screens.Animals.AnimalCatalogueUiState
import com.example.seepawandroid.ui.screens.animals.viewmodel.AnimalViewModel

@Composable
fun AnimalCatalogueScreen(
    viewModel: AnimalViewModel,
    isLoggedIn: Boolean,
    onAnimalClick: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadAnimals()
    }
    val uiState by viewModel.uiState.observeAsState(AnimalCatalogueUiState.Loading)
    var searchQuery by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

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

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.search(it)
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = stringResource(R.string.catalog_search_placeholder)
                    )
                },
                trailingIcon = {
                    Row {
                        IconButton(onClick = { /* filtros */ }) {
                            Icon(
                                Icons.Outlined.FilterList,
                                contentDescription = stringResource(R.string.catalog_filter)
                            )
                        }
                        IconButton(onClick = { /* ordenação */ }) {
                            Icon(
                                Icons.Outlined.Sort,
                                contentDescription = stringResource(R.string.catalog_sort)
                            )
                        }
                    }
                },
                placeholder = { Text(stringResource(R.string.catalog_search_placeholder)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (uiState) {

                is AnimalCatalogueUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is AnimalCatalogueUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                        Text(stringResource(R.string.catalog_empty))
                    }
                }

                is AnimalCatalogueUiState.Error -> {
                    val msg = (uiState as AnimalCatalogueUiState.Error).message
                    Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                        Text(stringResource(R.string.catalog_error, msg))
                    }
                }

                is AnimalCatalogueUiState.Success -> {
                    val animals = (uiState as AnimalCatalogueUiState.Success).animals

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(animals) { animal ->
                            AnimalCard(
                                name = animal.name,
                                age = animal.age,
                                imageUrl = animal.imageUrl,
                                isLoggedIn = isLoggedIn,
                                isFavorite = false,//atualizar depois de implementar favoritos
                                onClick = { onAnimalClick(animal.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}