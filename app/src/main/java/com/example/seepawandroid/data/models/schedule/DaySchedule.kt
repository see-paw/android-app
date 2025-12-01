package com.example.seepawandroid.data.models.schedule

import java.time.LocalDate

data class DaySchedule(
    val date: LocalDate,
    val slots: List<Slot>
)