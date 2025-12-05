package com.example.seepawandroid.data.remote.dtos.fosterings

import com.example.seepawandroid.data.remote.dtos.images.ResImageDto

/**
 * Response DTO for an active fostering.
 *
 * Received from GET /api/fosterings
 *
 * @property animalName Name of the fostered animal.
 * @property animalAge Age of the animal in years.
 * @property images List of images associated with the animal.
 * @property amount Monthly contribution amount.
 * @property startDate Date when the fostering started (ISO format).
 */
data class ResActiveFosteringDto(
    val animalName: String,
    val animalAge: Int,
    val images: List<ResImageDto>?,
    val amount: Double,
    val startDate: String
)