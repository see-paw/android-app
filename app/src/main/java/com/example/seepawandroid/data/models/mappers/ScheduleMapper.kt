package com.example.seepawandroid.data.models.mappers

import com.example.seepawandroid.data.models.activities.DaySchedule
import com.example.seepawandroid.data.models.activities.Schedule
import com.example.seepawandroid.data.models.activities.Slot
import com.example.seepawandroid.data.remote.dtos.activities.ResDayScheduleDto
import com.example.seepawandroid.data.remote.dtos.activities.ResScheduleResponseDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun ResDayScheduleDto.toDaySchedule(): DaySchedule {
    val date = LocalDate.parse(this.date)

    return DaySchedule(
        date = date,
        slots = this.toSlots(date)
    )
}

fun ResScheduleResponseDto.toSchedule() : Schedule {
    val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val weekStartDate = LocalDate.parse(this.startDate, inputFormatter)

    return Schedule(
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