package com.example.seepawandroid.data.remote.dtos.fosterings

/**
 * Lightweight DTO containing only fostering and animal IDs.
 *
 * Received from GET /api/fosterings/ids
 *
 * @property id Unique identifier of the fostering record.
 * @property animalId Unique identifier of the fostered animal.
 */
data class ResActiveFosteringIdDto(
    val id: String,
    val animalId: String
)