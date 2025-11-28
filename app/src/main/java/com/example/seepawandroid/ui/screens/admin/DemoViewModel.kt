package com.example.seepawandroid.ui.screens.admin

import androidx.lifecycle.ViewModel
import com.example.seepawandroid.data.managers.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DemoViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel()