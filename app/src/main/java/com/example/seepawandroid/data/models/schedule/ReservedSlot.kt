package com.example.seepawandroid.data.models.schedule

import java.time.LocalDateTime

/**
 * Represents a reserved time slot for animal visit scheduling.
 *
 * @property id The unique identifier of the slot.
 * @property start The start time of the slot.
 * @property end The end time of the slot.
 * @property isOwnReservation Indicates if the slot is reserved by the current user.
 * @property reservedBy The name of the user who reserved this slot.
 */
data class ReservedSlot(
    override val id: String,
    override val start: LocalDateTime,
    override val end: LocalDateTime,
    val isOwnReservation: Boolean,
    val reservedBy: String,
) : Slot()
