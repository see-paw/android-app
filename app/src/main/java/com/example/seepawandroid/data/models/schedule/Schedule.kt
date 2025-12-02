package com.example.seepawandroid.data.models.schedule

import java.time.LocalDate

/**
 * Represents a weekly schedule for an animal.
 *
 * @property animalId The unique identifier of the animal.
 * @property animalName The name of the animal.
 * @property shelterId The unique identifier of the shelter.
 * @property shelterName The name of the shelter.
 * @property weekStartDate The start date of the week for this schedule.
 * @property days List of day schedules for the week.
 */
data class Schedule(
    val animalId: String,
    val animalName: String,
    val shelterId: String,
    val shelterName: String,
    val weekStartDate: LocalDate,
    val days: List<DaySchedule>
)