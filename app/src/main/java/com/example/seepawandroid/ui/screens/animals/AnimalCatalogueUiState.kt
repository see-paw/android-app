package com.example.seepawandroid.ui.screens.Animals

import com.example.seepawandroid.data.local.entities.Animal

sealed class AnimalCatalogueUiState {
    object Loading : AnimalCatalogueUiState()
    data class Success(val animals: List<Animal>) : AnimalCatalogueUiState()
    data class Error(val message: String) : AnimalCatalogueUiState()
    object Empty : AnimalCatalogueUiState()
}
