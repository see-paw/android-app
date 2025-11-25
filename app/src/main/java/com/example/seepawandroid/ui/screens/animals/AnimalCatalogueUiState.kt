package com.example.seepawandroid.ui.screens.Animals

import com.example.seepawandroid.data.local.entities.Animal

/**
 * Represents the UI state for the Animal Catalogue screen.
 *
 * Used by the ViewModel to communicate:
 * - Loading state
 * - Successful data load
 * - Empty results
 * - Error messages
 */
sealed class AnimalCatalogueUiState {

    /** Indicates that data is being loaded. */
    object Loading : AnimalCatalogueUiState()

    /**
     * Success state containing the loaded list of animals and pagination info.
     *
     * @property animals List of animals for the current page.
     * @property currentPage Page index currently displayed.
     * @property totalPages Total number of pages available.
     * @property totalCount Total number of animals available.
     */
    data class Success(
        val animals: List<Animal>,
        val currentPage: Int,
        val totalPages: Int,
        val totalCount: Int
    ) : AnimalCatalogueUiState()

    /**
     * Error state containing a descriptive message.
     */
    data class Error(val message: String) : AnimalCatalogueUiState()

    /**
     * Indicates that the query returned no animals.
     */
    object Empty : AnimalCatalogueUiState()
}
