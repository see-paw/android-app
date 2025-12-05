package com.example.seepawandroid.data.models.mappers

import com.example.seepawandroid.data.models.schedule.DaySchedule
import com.example.seepawandroid.data.models.schedule.Schedule
import com.example.seepawandroid.data.models.schedule.Slot
import com.example.seepawandroid.data.remote.dtos.schedule.ResDayScheduleDto
import com.example.seepawandroid.data.remote.dtos.schedule.ResScheduleResponseDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Converts a ResDayScheduleDto from the API to a DaySchedule domain model.
 *
 * @return DaySchedule with parsed date and converted slots.
 */
fun ResDayScheduleDto.toDaySchedule(): DaySchedule {
    val date = LocalDate.parse(this.date)

    return DaySchedule(
        date = date,
        slots = this.toSlots(date)
    )
}

/**
 * Converts a ResScheduleResponseDto from the API to a Schedule domain model.
 *
 * @return Schedule with parsed week start date and converted days.
 */
fun ResScheduleResponseDto.toSchedule() : Schedule {
    val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val weekStartDate = LocalDate.parse(this.startDate, inputFormatter)

    return Schedule(
        animalId = this.animal.id,
        animalName = this.animal.name,
        shelterId = this.shelter.id,
        shelterName = this.shelter.name,
        weekStartDate = weekStartDate,
        days = days.map { it.toDaySchedule() }
    )
}

private fun ResDayScheduleDto.toSlots(date: LocalDate): List<Slot> =
    availableSlots.map { it.toAvailableSlot(date) } +
            reservedSlots.map { it.toReservedSlot(date) } +
            unavailableSlots.map { it.toUnavailableSlot(date) }