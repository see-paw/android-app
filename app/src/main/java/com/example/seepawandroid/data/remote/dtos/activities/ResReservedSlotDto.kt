package com.example.seepawandroid.data.remote.dtos.activities

data class ResReservedSlotDto(
    val id: String,
    val start: String,
    val end: String,
    val isOwnReservation: Boolean,
    val reservedBy: String,
)