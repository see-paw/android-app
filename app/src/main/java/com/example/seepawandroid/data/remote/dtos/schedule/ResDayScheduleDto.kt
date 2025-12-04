package com.example.seepawandroid.data.remote.dtos.schedule

/**
 * Response DTO representing a day's schedule from the API.
 *
 * @property availableSlots List of available time slots for this day.
 * @property date The date in ISO-8601 format.
 * @property reservedSlots List of reserved time slots for this day.
 * @property unavailableSlots List of unavailable time slots for this day.
 */
data class ResDayScheduleDto(
    val availableSlots: List<ResAvailableSlotDto>,
    val date: String,
    val reservedSlots: List<ResReservedSlotDto>,
    val unavailableSlots: List<ResUnavailableSlotDto>
)
