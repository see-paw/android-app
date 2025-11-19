package com.example.seepawandroid.ui.screens.Animals

import com.example.seepawandroid.data.local.entities.Animal

sealed class AnimalListUiState {
    object Loading : AnimalListUiState()
    data class Success(val animals: List<Animal>) : AnimalListUiState()
    data class Error(val message: String) : AnimalListUiState()
    object Empty : AnimalListUiState()
}
