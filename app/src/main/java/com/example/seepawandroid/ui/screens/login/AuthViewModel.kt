package com.example.seepawandroid.ui.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.seepawandroid.data.providers.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel // ← ADICIONAR
class AuthViewModel @Inject constructor(
    private val sessionManager: SessionManager
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

    fun onLoginSuccess() {
        checkAuthState()
    }

    /**
     * Logs out the current user.
     *
     * Clears all session data and updates authentication state.
     */
    fun logout() {
        sessionManager.clearSession()
        checkAuthState()
    }
}