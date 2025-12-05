package com.example.seepawandroid.data.models.schedule

import java.time.LocalDateTime

/**
 * Represents an unavailable time slot for animal visit scheduling.
 *
 * @property id The unique identifier of the slot.
 * @property start The start time of the slot.
 * @property end The end time of the slot.
 * @property reason The reason why this slot is unavailable.
 */
data class UnavailableSlot(
    override val id: String,
    override val start: LocalDateTime,
    override val end: LocalDateTime,
    val reason: String
) : Slot()
