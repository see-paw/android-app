package com.example.seepawandroid.ui.screens.schedule

import com.example.seepawandroid.data.models.schedule.AvailableSlot

/**
 * Represents the state of the modal dialog.
 */
sealed class ModalUiState {
    /**
     * The modal is hidden.
     */
    object Hidden : ModalUiState()
    /**
     * The modal is in the confirm state.
     *
     * @property slot The slot to be confirmed.
     * @property animalId The ID of the animal.
     * @property animalName The name of the animal.
     */
    data class Confirm(
        val slot: AvailableSlot,
        val animalId: String,
        val animalName: String
    ) : ModalUiState()

    /**
     * The modal is in the loading state.
     */
    object Loading : ModalUiState()

    /**
     * The modal is in the error state.
     *
     * @property message The error message.
     * @property slot The slot that caused the error.
     * @property animalId The ID of the animal.
     * @property animalName The name of the animal.
     */
    data class Error(
        val message: String,
        val slot: AvailableSlot,
        val animalId: String,
        val animalName: String
    ) : ModalUiState()
}