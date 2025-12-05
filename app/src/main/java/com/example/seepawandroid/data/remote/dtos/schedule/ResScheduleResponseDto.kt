package com.example.seepawandroid.data.remote.dtos.schedule

import com.example.seepawandroid.data.remote.dtos.animals.ResAnimalDto

/**
 * Response DTO representing a weekly schedule from the API.
 *
 * @property animal The animal information.
 * @property days List of day schedules for the week.
 * @property shelter The shelter information.
 * @property startDate The start date of the week in dd/MM/yyyy format.
 */
data class ResScheduleResponseDto(
    val animal: ResAnimalDto,
    val days: List<ResDayScheduleDto>,
    val shelter: SimpleShelterDto,
    val startDate: String
)
