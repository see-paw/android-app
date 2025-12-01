package com.example.seepawandroid.data.remote.dtos.schedule

data class ResUnavailableSlotDto(
    val id: String,
    val start: String,
    val end: String,
    val reason: String
)