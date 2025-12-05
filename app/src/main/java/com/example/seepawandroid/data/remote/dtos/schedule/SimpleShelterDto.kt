package com.example.seepawandroid.data.remote.dtos.schedule

/**
 * Simplified shelter DTO used in schedule responses.
 *
 * @property id The unique identifier of the shelter.
 * @property name The name of the shelter.
 */
data class SimpleShelterDto(
    val id: String,
    val name: String
)
