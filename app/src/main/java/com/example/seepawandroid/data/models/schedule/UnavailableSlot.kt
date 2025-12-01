package com.example.seepawandroid.data.models.schedule

import java.time.LocalDateTime

data class UnavailableSlot(
    override val id: String,
    override val start: LocalDateTime,
    override val end: LocalDateTime,
    val reason: String
) : Slot()
