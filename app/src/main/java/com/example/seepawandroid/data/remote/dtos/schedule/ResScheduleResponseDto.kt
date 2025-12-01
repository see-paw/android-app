package com.example.seepawandroid.data.remote.dtos.schedule

import com.example.seepawandroid.data.remote.dtos.animals.ResAnimalDto

data class ResScheduleResponseDto(
    val animal: ResAnimalDto,
    val days: List<ResDayScheduleDto>,
    val shelter: SimpleShelterDto,
    val startDate: String
)