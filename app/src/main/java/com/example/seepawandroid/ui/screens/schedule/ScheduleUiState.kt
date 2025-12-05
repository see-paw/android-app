package com.example.seepawandroid.ui.screens.schedule

import com.example.seepawandroid.data.models.schedule.Schedule
import com.example.seepawandroid.data.models.schedule.Slot
import java.time.LocalDate

/**
 * Represents the state of the schedule screen.
 */
sealed class ScheduleUiState {

    /**
     * The schedule is loading.
     */
    object Loading : ScheduleUiState()

    /**
     * The schedule was loaded successfully.
     *
     * @property schedule The loaded schedule.
     * @property selectedSlot The currently selected slot.
     */
    data class Success (
        val schedule: Schedule,
        val selectedSlot: Slot? = null
    ) : ScheduleUiState()

    /**
     * An error occurred while loading the schedule.
     *
     * @property message The error message.
     * @property animalId The ID of the animal for which the schedule was being loaded.
     * @property startDate The start date of the week for which the schedule was being loaded.
     */
    data class Error(
        val message: String,
        val animalId: String,
        val startDate: LocalDate
    ) : ScheduleUiState()
}