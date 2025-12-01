package com.example.seepawandroid.ui.screens.schedule

import com.example.seepawandroid.data.models.schedule.Schedule
import com.example.seepawandroid.data.models.schedule.Slot
import java.time.LocalDate

sealed class ScheduleUiState {

    object Loading : ScheduleUiState()

    data class Success (
        val schedule: Schedule,
        val selectedSlot: Slot? = null
    ) : ScheduleUiState()

    data class Error(
        val message: String,
        val animalId: String,
        val startDate: LocalDate
    ) : ScheduleUiState()
}