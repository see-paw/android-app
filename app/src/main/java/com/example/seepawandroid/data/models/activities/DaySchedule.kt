package com.example.seepawandroid.data.models.activities

import java.time.LocalDate

data class DaySchedule(
    val date: LocalDate,
    val slots: List<Slot>
)