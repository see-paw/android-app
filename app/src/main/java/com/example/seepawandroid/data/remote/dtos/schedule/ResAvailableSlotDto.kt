package com.example.seepawandroid.data.remote.dtos.schedule

/**
 * Response DTO representing an available time slot from the API.
 *
 * @property id The unique identifier of the slot.
 * @property start The start time in HH:mm format.
 * @property end The end time in HH:mm format.
 */
data class ResAvailableSlotDto(
    val id: String,
    val start: String,
    val end: String
)
