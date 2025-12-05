package com.example.seepawandroid.ui.screens.ownerships

/**
 * Represents the UI state for the Ownership Request flow.
 *
 * This is a wizard-style flow with multiple steps:
 * 1. ShowingTerms - User must scroll and accept terms
 * 2. ShowingForm - User fills mock form data
 * 3. Success - Request submitted successfully
 * 4. Error - Something went wrong
 */
sealed class OwnershipRequestUiState {

    /**
     * Initial loading state - fetching user data.
     */
    object Loading : OwnershipRequestUiState()

    /**
     * Showing terms and conditions screen.
     * User must scroll to bottom to enable accept button.
     *
     * @property animalId ID of the animal being requested.
     * @property animalName Name of the animal.
     * @property hasScrolledToBottom Whether user has scrolled to the end.
     */
    data class ShowingTerms(
        val animalId: String,
        val animalName: String,
        val hasScrolledToBottom: Boolean = false
    ) : OwnershipRequestUiState()

    /**
     * Showing mock form with auto-filled data.
     * User only needs to click submit.
     *
     * @property animalId ID of the animal being requested.
     * @property animalName Name of the animal.
     * @property accountNumber Mock account number.
     * @property holderName Mock account holder name.
     * @property cvv Mock CVV.
     * @property citizenCard Mock citizen card number.
     * @property password Mock password (displayed as dots).
     * @property isSubmitting Whether request is being submitted.
     */
    data class ShowingForm(
        val animalId: String,
        val animalName: String,
        val accountNumber: String,
        val holderName: String,
        val cvv: String,
        val citizenCard: String,
        val password: String,
        val isSubmitting: Boolean = false
    ) : OwnershipRequestUiState()

    /**
     * Success state - request submitted successfully.
     *
     * @property userName Name of the user who made the request.
     * @property animalName Name of the adopted animal.
     * @property shelterName Name of the shelter (placeholder for now).
     */
    data class Success(
        val userName: String,
        val animalName: String,
        val shelterName: String = "Animais Fofos" // TODO: Get from animal data
    ) : OwnershipRequestUiState()

    /**
     * Error state - failed to submit request.
     *
     * @property message Error message to display.
     */
    data class Error(
        val message: String
    ) : OwnershipRequestUiState()
}