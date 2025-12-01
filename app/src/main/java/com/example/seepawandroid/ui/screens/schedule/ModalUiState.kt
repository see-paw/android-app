package com.example.seepawandroid.ui.screens.schedule

import com.example.seepawandroid.data.models.schedule.AvailableSlot

sealed class ModalUiState {
    object Hidden : ModalUiState()
    data class Confirm(
        val slot: AvailableSlot,
        val animalId: String,
        val animalName: String
    ) : ModalUiState()

    object Loading : ModalUiState()

    data class Error(
        val message: String,
        val slot: AvailableSlot,
        val animalId: String,
        val animalName: String
    ) : ModalUiState()
}