package com.example.seepawandroid.data.models.mappers

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
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Converts a ResAvailableSlotDto to an AvailableSlot domain model.
 *
 * @param date The date for the slot.
 * @return AvailableSlot with parsed start and end times.
 */
fun ResAvailableSlotDto.toAvailableSlot(date: LocalDate) : AvailableSlot = AvailableSlot(
    id = this.id,
    start = parseDateTime(date, this.start),
    end = parseDateTime(date, this.end),
)

/**
 * Converts a ResReservedSlotDto to a ReservedSlot domain model.
 *
 * @param date The date for the slot.
 * @return ReservedSlot with parsed start and end times and reservation details.
 */
fun ResReservedSlotDto.toReservedSlot(date: LocalDate) : ReservedSlot = ReservedSlot(
    id = this.id,
    start = parseDateTime(date, this.start),
    end = parseDateTime(date, this.end),
    isOwnReservation = this.isOwnReservation,
    reservedBy = this.reservedBy,
)

/**
 * Converts a ResUnavailableSlotDto to an UnavailableSlot domain model.
 *
 * @param date The date for the slot.
 * @return UnavailableSlot with parsed start and end times and unavailability reason.
 */
fun ResUnavailableSlotDto.toUnavailableSlot(date: LocalDate) : UnavailableSlot = UnavailableSlot(
    id = this.id,
    start = parseDateTime(date, this.start),
    end = parseDateTime(date, this.end),
    reason = this.reason,
)

/**
 * Converts an AvailableSlot to a ReqCreateOwnershipActivityDto for API submission.
 *
 * @param animalId The ID of the animal for the ownership activity.
 * @return ReqCreateOwnershipActivityDto with UTC-formatted timestamps.
 */
fun AvailableSlot.toReqCreateOwnershipDto(animalId: String) : ReqCreateOwnershipActivityDto {
    val formatter = DateTimeFormatter.ISO_INSTANT
    val utcZone = ZoneId.of("UTC")

    return ReqCreateOwnershipActivityDto(
        animalId = animalId,
        startDate = this.start.atZone(ZoneId.systemDefault())
            .withZoneSameInstant(utcZone)
            .format(formatter),
        endDate = this.end.atZone(ZoneId.systemDefault())
            .withZoneSameInstant(utcZone)
            .format(formatter)
    )
}


private fun parseDateTime(date: LocalDate, time: String) : LocalDateTime {
    val localTime = LocalTime.parse(time)
    return LocalDateTime.of(date, localTime)
}