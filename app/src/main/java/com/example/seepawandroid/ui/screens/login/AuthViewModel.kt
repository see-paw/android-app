package com.example.seepawandroid.ui.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.managers.NotificationManager
import com.example.seepawandroid.data.managers.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val notificationManager: NotificationManager
) : ViewModel() {

    private val _isAuthenticated = MutableLiveData(sessionManager.isAuthenticated())
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    private val _userRole = MutableLiveData(sessionManager.getUserRole() ?: "")
    val userRole: LiveData<String> = _userRole

    fun checkAuthState() {
        _isAuthenticated.value = sessionManager.isAuthenticated()
        _userRole.value = sessionManager.getUserRole() ?: ""

        // DEBUG
        android.util.Log.d("AuthViewModel", "isAuthenticated: ${_isAuthenticated.value}")
        android.util.Log.d("AuthViewModel", "role: ${_userRole.value}")
    }

    /**
     * Called after successful login.
     * Initializes all state managers with user data.
     */
    fun onLoginSuccess() {
        checkAuthState()

        // Initialize ownership state and notifications
        viewModelScope.launch(Dispatchers.IO) {
            try {
                notificationManager.initializeOnLogin()
                android.util.Log.d("AuthViewModel", "NotificationManager initialized successfully")
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Error initializing NotificationManager", e)
            }
        }
    }

    /**
     * Logs out the current user.
     *
     * Clears all session data and updates authentication state.
     */
    fun logout() {
        // Cleanup state managers before clearing session
        notificationManager.cleanupOnLogout()

        // Clear session
        sessionManager.clearSession()

        // Update auth state
        checkAuthState()

        android.util.Log.d("AuthViewModel", "User logged out successfully")
    }
}