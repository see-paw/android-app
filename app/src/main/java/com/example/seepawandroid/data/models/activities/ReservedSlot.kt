package com.example.seepawandroid.data.models.activities

import java.time.LocalDateTime

data class ReservedSlot(
    override val id: String,
    override val start: LocalDateTime,
    override val end: LocalDateTime,
    val isOwnReservation: Boolean,
    val reservedBy: String,
) : Slot()
