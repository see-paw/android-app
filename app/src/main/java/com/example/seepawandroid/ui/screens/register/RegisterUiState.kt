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
     * Represents the error state of the registration process.
     *
     * @property message The error message to be displayed.
     */
    data class Error(val message: String) : RegisterUiState()
}