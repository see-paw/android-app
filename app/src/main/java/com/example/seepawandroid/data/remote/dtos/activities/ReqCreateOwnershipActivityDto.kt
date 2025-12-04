package com.example.seepawandroid.data.remote.dtos.activities

/**
 * Request DTO for creating a new ownership activity.
 *
 * @property animalId The unique identifier of the animal.
 * @property endDate The end date and time in ISO-8601 format (UTC).
 * @property startDate The start date and time in ISO-8601 format (UTC).
 */
data class ReqCreateOwnershipActivityDto(
    val animalId: String,
    val endDate: String,
    val startDate: String
)