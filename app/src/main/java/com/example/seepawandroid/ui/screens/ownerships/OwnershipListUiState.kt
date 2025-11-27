package com.example.seepawandroid.ui.screens.ownership

import com.example.seepawandroid.data.remote.dtos.animals.ResOwnedAnimalDto
import com.example.seepawandroid.data.remote.dtos.ownerships.ResOwnershipRequestListDto

/**
 * UI State for Ownership List Screen.
 *
 * Represents different states of the ownership requests list.
 */
sealed class OwnershipListUiState {
    /**
     * Loading state - fetching data from backend or cache.
     */
    object Loading : OwnershipListUiState()

    /**
     * Success state with list of ownership requests.
     *
     * @param requests List of user's ownership requests, sorted by date (newest first).
     * @param isRefreshing Whether a pull-to-refresh is in progress.
     */
    data class Success(
        val activeRequests: List<ResOwnershipRequestListDto>,
        val ownedAnimals: List<ResOwnedAnimalDto>,
        val isRefreshing: Boolean = false
    ) : OwnershipListUiState()

    /**
     * Empty state - user has no ownership requests.
     */
    object Empty : OwnershipListUiState()

    /**
     * Error state - failed to load ownership requests.
     *
     * @param message Error message to display.
     */
    data class Error(val message: String) : OwnershipListUiState()
}