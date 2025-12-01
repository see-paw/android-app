package com.example.seepawandroid.data.models.mappers

import com.example.seepawandroid.data.models.schedule.DaySchedule
import com.example.seepawandroid.data.models.schedule.Schedule
import com.example.seepawandroid.data.models.schedule.Slot
import com.example.seepawandroid.data.remote.dtos.schedule.ResDayScheduleDto
import com.example.seepawandroid.data.remote.dtos.schedule.ResScheduleResponseDto
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