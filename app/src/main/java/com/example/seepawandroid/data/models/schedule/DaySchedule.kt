package com.example.seepawandroid.data.models.schedule

import java.time.LocalDate

/**
 * Represents the schedule for a single day.
 *
 * @property date The date of the schedule.
 * @property slots List of time slots for this day.
 */
data class DaySchedule(
    val date: LocalDate,
    val slots: List<Slot>
)