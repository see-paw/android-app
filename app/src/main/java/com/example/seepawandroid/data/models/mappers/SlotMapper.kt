package com.example.seepawandroid.data.models.mappers

import com.example.seepawandroid.data.models.activities.AvailableSlot
import com.example.seepawandroid.data.models.activities.ReservedSlot
import com.example.seepawandroid.data.models.activities.UnavailableSlot
import com.example.seepawandroid.data.remote.dtos.activities.ResAvailableSlotDto
import com.example.seepawandroid.data.remote.dtos.activities.ResReservedSlotDto
import com.example.seepawandroid.data.remote.dtos.activities.ResUnavailableSlotDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun ResAvailableSlotDto.toAvailableSlot(date: LocalDate) : AvailableSlot = AvailableSlot(
    id = this.id,
    start = parseDateTime(date, this.start),
    end = parseDateTime(date, this.end),
)

fun ResReservedSlotDto.toReservedSlot(date: LocalDate) : ReservedSlot = ReservedSlot(
    id = this.id,
    start = parseDateTime(date, this.start),
    end = parseDateTime(date, this.end),
    isOwnReservation = this.isOwnReservation,
    reservedBy = this.reservedBy,
)

fun ResUnavailableSlotDto.toUnavailableSlot(date: LocalDate) : UnavailableSlot = UnavailableSlot(
    id = this.id,
    start = parseDateTime(date, this.start),
    end = parseDateTime(date, this.end),
    reason = this.reason,
)

private fun parseDateTime(date: LocalDate, time: String) : LocalDateTime {
    val localTime = LocalTime.parse(time)
    return LocalDateTime.of(date, localTime)
}