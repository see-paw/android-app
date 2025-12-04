package com.example.seepawandroid.data.models.schedule

import java.time.LocalDateTime

/**
 * Represents an available time slot for animal visit scheduling.
 */
data class AvailableSlot(
    override val id: String,
    override val start: LocalDateTime,
    override val end: LocalDateTime
) : Slot()
