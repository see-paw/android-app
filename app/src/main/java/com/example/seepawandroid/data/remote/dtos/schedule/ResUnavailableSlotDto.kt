package com.example.seepawandroid.data.remote.dtos.schedule

/**
 * Response DTO representing an unavailable time slot from the API.
 *
 * @property id The unique identifier of the slot.
 * @property start The start time in HH:mm format.
 * @property end The end time in HH:mm format.
 * @property reason The reason why this slot is unavailable.
 */
data class ResUnavailableSlotDto(
    val id: String,
    val start: String,
    val end: String,
    val reason: String
)
