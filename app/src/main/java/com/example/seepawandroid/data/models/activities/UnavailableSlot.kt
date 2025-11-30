package com.example.seepawandroid.data.models.activities

import java.time.LocalDateTime

data class UnavailableSlot(
    override val id: String,
    override val start: LocalDateTime,
    override val end: LocalDateTime,
    val reason: String
) : Slot()
