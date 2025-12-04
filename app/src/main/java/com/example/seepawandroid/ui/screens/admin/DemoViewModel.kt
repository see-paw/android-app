package com.example.seepawandroid.ui.screens.admin

import androidx.lifecycle.ViewModel
import com.example.seepawandroid.data.managers.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for demonstration purposes.
 */
@HiltViewModel
class DemoViewModel @Inject constructor(
    /**
     * The manager for the current user session.
     */
    val sessionManager: SessionManager
) : ViewModel()