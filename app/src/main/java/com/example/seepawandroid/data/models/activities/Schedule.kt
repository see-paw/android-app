package com.example.seepawandroid.data.models.activities

import java.time.LocalDate

data class Schedule(
    val animalName: String,
    val shelterId: String,
    val shelterName: String,
    val weekStartDate: LocalDate,
    val days: List<DaySchedule>
)