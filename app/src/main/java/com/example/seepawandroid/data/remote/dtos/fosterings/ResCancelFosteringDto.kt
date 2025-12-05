package com.example.seepawandroid.data.remote.dtos.fosterings

/**
 * Response DTO for a cancelled fostering.
 *
 * Received from PATCH /api/fosterings/{id}/cancel
 *
 * @property animalName Name of the fostered animal.
 * @property animalAge Age of the animal in years.
 * @property amount Monthly contribution amount.
 * @property startDate Date when the fostering started.
 * @property endDate Date when the fostering was cancelled.
 */
data class ResCancelFosteringDto(
    val animalName: String,
    val animalAge: Int,
    val amount: Double,
    val startDate: String,
    val endDate: String
)