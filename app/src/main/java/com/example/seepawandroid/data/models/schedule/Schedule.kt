package com.example.seepawandroid.data.models.schedule

import java.time.LocalDate

data class Schedule(
    val animalId: String,
    val animalName: String,
    val shelterId: String,
    val shelterName: String,
    val weekStartDate: LocalDate,
    val days: List<DaySchedule>
)