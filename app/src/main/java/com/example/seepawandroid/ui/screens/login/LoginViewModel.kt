package com.example.seepawandroid.ui.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.providers.SessionManager
import com.example.seepawandroid.data.repositories.AuthRepository
import com.example.seepawandroid.data.repositories.UserRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the Login screen.
 *
 * Manages login business logic and UI state.
 * Communicates with AuthRepository to perform authentication.
 */
class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val userRepository = UserRepository()

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _uiState = MutableLiveData<LoginUiState>(LoginUiState.Idle)
    val uiState: LiveData<LoginUiState> = _uiState

    /**
     * Updates the email field value.
     *
     * @param newEmail New email value from user input
     */
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    /**
     * Updates the password field value.
     *
     * @param newPassword New password value from user input
     */
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    /**
     * Attempts to authenticate the user with the provided credentials.
     *
     * Performs the following steps:
     * Validates and sends login credentials to the backend
     * On successful authentication, fetches the user's role and ID
     * Stores the role and ID in SessionManager
     * Updates UI state accordingly
     */
    fun login() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            // Authenticate user
            val loginResult = repository.login(_email.value ?: "", _password.value ?: "")

            if (loginResult.isSuccess) {
                //Fetch user role and ID
                val roleResult = userRepository.fetchUserRole()
                val userIdResult = userRepository.fetchUserId()

                if (roleResult.isSuccess && userIdResult.isSuccess) {
                    val role = roleResult.getOrNull() ?: "User"
                    val userId = userIdResult.getOrNull() ?: ""

                    // Save role and userId
                    SessionManager.saveUserRole(role)
                    SessionManager.saveUserId(userId)

                    // Update UI state
                    _uiState.value = LoginUiState.Success(userId, role)
                } else {
                    _uiState.value = LoginUiState.Error("Failed to fetch user data")
                }
            } else {
                _uiState.value = LoginUiState.Error(
                    loginResult.exceptionOrNull()?.message ?: "Unknown error"
                )
            }
        }
    }

    /**
     * Resets UI state back to Idle.
     *
     * Useful for clearing error messages after user dismisses them.
     */
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}