package com.example.seepawandroid.data.models.domain

import com.example.seepawandroid.data.remote.dtos.images.ResImageDto

/**
 * Domain model for an active fostering with complete information.
 *
 * Combines data from ResActiveFosteringDto and ResActiveFosteringIdDto.
 *
 * @property id Unique identifier of the fostering record.
 * @property animalId Unique identifier of the fostered animal.
 * @property animalName Name of the fostered animal.
 * @property animalAge Age of the animal in years.
 * @property images List of images associated with the animal.
 * @property amount Monthly contribution amount.
 * @property startDate Date when the fostering started (ISO format).
 */
data class ActiveFostering(
    val id: String,
    val animalId: String,
    val animalName: String,
    val animalAge: Int,
    val images: List<ResImageDto>?,
    val amount: Double,
    val startDate: String
)