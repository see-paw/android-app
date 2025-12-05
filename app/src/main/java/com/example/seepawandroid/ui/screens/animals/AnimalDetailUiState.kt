package com.example.seepawandroid.ui.screens.animals

import com.example.seepawandroid.data.remote.dtos.animals.ResAnimalDto

/**
 * Represents the UI state for the Animal Detail screen.
 *
 * States:
 * - Loading: Fetching animal data
 * - Success: Animal loaded successfully (with internet)
 * - SuccessOffline: Animal loaded from Room (without internet, actions disabled)
 * - Error: Failed to load animal
 */
sealed class AnimalDetailUiState {

    /** Indicates that animal data is being loaded. */
    object Loading : AnimalDetailUiState()

    /**
     * Success state - animal loaded from backend.
     * User has internet, all actions are available.
     *
     * @property animal Full animal data from backend.
     * @property canFoster Whether fostering action is available.
     * @property canRequestOwnership Whether ownership request action is available.
     */
    data class Success(
        val animal: ResAnimalDto,
        val canFoster: Boolean,
        val canRequestOwnership: Boolean
    ) : AnimalDetailUiState()

    /**
     * Success state - animal loaded from local Room database.
     * User has NO internet, action buttons are disabled.
     *
     * @property animal Animal data from Room.
     * @property canFoster Always false (offline).
     * @property canRequestOwnership Always false (offline).
     */
    data class SuccessOffline(
        val animal: ResAnimalDto,
        val canFoster: Boolean = false,
        val canRequestOwnership: Boolean = false
    ) : AnimalDetailUiState()

    /**
     * Error state - failed to load animal.
     *
     * @property message Error message to display.
     * @property needsInternet If true, show "needs internet" specific message.
     */
    data class Error(
        val message: String,
        val needsInternet: Boolean = false
    ) : AnimalDetailUiState()
}
