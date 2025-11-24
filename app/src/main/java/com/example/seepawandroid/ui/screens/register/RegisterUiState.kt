package com.example.seepawandroid.ui.screens.register

/**
 * Represents the different UI states for the registration screen.
 */
sealed class RegisterUiState {
    /**
     * Initial state - no registration attempt yet.
     */
    object Idle : RegisterUiState()

    /**
     * Registration is in progress.
     */
    object Loading : RegisterUiState()

    /**
     * Registration completed successfully.
     */
    object Success : RegisterUiState()

    /**
     * Registration failed with an error.
     *
     * @param message Error message to display to the user
     */
    data class Error(val message: String) : RegisterUiState()
}