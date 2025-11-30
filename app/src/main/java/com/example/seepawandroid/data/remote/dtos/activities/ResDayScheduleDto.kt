package com.example.seepawandroid.data.remote.dtos.activities

data class ResDayScheduleDto(
    val availableSlots: List<ResAvailableSlotDto>,
    val date: String,
    val reservedSlots: List<ResReservedSlotDto>,
    val unavailableSlots: List<ResUnavailableSlotDto>
)