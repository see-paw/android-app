package com.example.seepawandroid.ui.screens.activities

import com.example.seepawandroid.data.models.activities.Schedule
import com.example.seepawandroid.data.models.activities.Slot

sealed class ScheduleUiState {

    object Loading : ScheduleUiState()

    data class Success (
        val schedule: Schedule,
        val selectedSlot: Slot? = null
    ) : ScheduleUiState()

    data class Error(
        val message: String,
    ) : ScheduleUiState()
}