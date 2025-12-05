package com.example.seepawandroid.ui.screens.fosterings

import com.example.seepawandroid.data.remote.dtos.fosterings.ResActiveFosteringDto

/**
 * UI State for Fostering List Screen.
 *
 * Represents different states of the fostering list.
 */
sealed class FosteringListUiState {
    /**
     * Loading state - fetching data from backend.
     */
    object Loading : FosteringListUiState()

    /**
     * Success state containing the list of active fosterings.
     *
     * @property fosterings The list of active fosterings.
     * @property isRefreshing Whether a pull-to-refresh is in progress.
     */
    data class Success(
        val fosterings: List<ResActiveFosteringDto>,
        val isRefreshing: Boolean = false
    ) : FosteringListUiState()

    /**
     * Empty state - user has no active fosterings.
     */
    object Empty : FosteringListUiState()

    /**
     * Error state.
     *
     * @property message The error message to be displayed.
     */
    data class Error(val message: String) : FosteringListUiState()
}