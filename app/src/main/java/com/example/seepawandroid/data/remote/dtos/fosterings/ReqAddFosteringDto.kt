package com.example.seepawandroid.data.remote.dtos.fosterings

/**
 * Request DTO for creating a new fostering.
 *
 * Sent to POST /api/fosterings/{animalId}/fosterings
 *
 * @property monthValue The monthly contribution amount in euros.
 */
data class ReqAddFosteringDto(
    val monthValue: Double
)