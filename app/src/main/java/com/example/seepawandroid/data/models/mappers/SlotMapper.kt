package com.example.seepawandroid.data.models.mappers

import android.R
import com.example.seepawandroid.data.models.schedule.AvailableSlot
import com.example.seepawandroid.data.models.schedule.ReservedSlot
import com.example.seepawandroid.data.models.schedule.UnavailableSlot
import com.example.seepawandroid.data.remote.dtos.activities.ReqCreateOwnershipActivityDto
import com.example.seepawandroid.data.remote.dtos.schedule.ResAvailableSlotDto
import com.example.seepawandroid.data.remote.dtos.schedule.ResReservedSlotDto
import com.example.seepawandroid.data.remote.dtos.schedule.ResUnavailableSlotDto
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

fun AvailableSlot.toReqCreateOwnershipDto(animalId: String) : ReqCreateOwnershipActivityDto =
    ReqCreateOwnershipActivityDto(
        animalId = animalId,
        startDate = this.start.toString(),
        endDate = this.end.toString(),
    )


private fun parseDateTime(date: LocalDate, time: String) : LocalDateTime {
    val localTime = LocalTime.parse(time)
    return LocalDateTime.of(date, localTime)
}