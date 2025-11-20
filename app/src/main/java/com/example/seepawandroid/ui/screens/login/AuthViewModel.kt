package com.example.seepawandroid.ui.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.seepawandroid.data.providers.SessionManager
import com.example.seepawandroid.data.repositories.AuthRepository
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isAuthenticated = MutableLiveData(SessionManager.isAuthenticated())
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    private val _userRole = MutableLiveData(SessionManager.getUserRole() ?: "")
    val userRole: LiveData<String> = _userRole

    fun checkAuthState() {
        _isAuthenticated.value = SessionManager.isAuthenticated()
        _userRole.value = SessionManager.getUserRole() ?: ""

        // DEBUG
        android.util.Log.d("AuthViewModel", "isAuthenticated: ${_isAuthenticated.value}")
        android.util.Log.d("AuthViewModel", "role: ${_userRole.value}")
    }

    fun onLoginSuccess() {
        checkAuthState()
    }

    /**
     * Logs out the current user.
     *
     * Clears all session data and updates authentication state.
     */
    fun logout() {
        SessionManager.clearSession()
        checkAuthState()
    }
}