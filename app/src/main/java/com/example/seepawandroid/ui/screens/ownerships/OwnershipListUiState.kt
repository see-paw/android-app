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
     * Represents the success state, containing the lists of active requests and owned animals.
     *
     * @property activeRequests The list of active ownership requests.
     * @property ownedAnimals The list of animals owned by the user.
     * @property isRefreshing Whether a pull-to-refresh is in progress.
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
     * Represents the error state.
     *
     * @property message The error message to be displayed.
     */
    data class Error(val message: String) : OwnershipListUiState()
}