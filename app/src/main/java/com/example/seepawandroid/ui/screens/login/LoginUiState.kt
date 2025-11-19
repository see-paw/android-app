package com.example.seepawandroid.ui.screens.login

/**
 * Sealed class representing all possible UI states for the Login screen.
 *
 * Using a sealed class ensures all states are handled explicitly in when expressions,
 * preventing unhandled state bugs.
 */
sealed class LoginUiState {
    /**
     * Initial state - user hasn't attempted login yet.
     */
    object Idle : LoginUiState()

    /**
     * Login request is in progress.
     */
    object Loading : LoginUiState()

    /**
     * Login was successful.
     *
     * @property userId User's unique identifier
     * @property role User's role (User or AdminCAA)
     */
    data class Success(val userId: String, val role: String) : LoginUiState()

    /**
     * Login failed with an error.
     *
     * @property message Error message to display to user
     */
    data class Error(val message: String) : LoginUiState()
}