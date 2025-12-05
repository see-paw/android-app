package com.example.seepawandroid.data.remote.dtos.schedule

/**
 * Response DTO representing a reserved time slot from the API.
 *
 * @property id The unique identifier of the slot.
 * @property start The start time in HH:mm format.
 * @property end The end time in HH:mm format.
 * @property isOwnReservation Indicates if the current user made this reservation.
 * @property reservedBy The name of the user who reserved this slot.
 */
data class ResReservedSlotDto(
    val id: String,
    val start: String,
    val end: String,
    val isOwnReservation: Boolean,
    val reservedBy: String,
)
