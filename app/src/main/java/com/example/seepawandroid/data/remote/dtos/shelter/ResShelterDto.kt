package com.example.seepawandroid.data.remote.dtos.shelter

/**
 * Response DTO for shelter information.
 *
 * Received from GET /api/Shelters/{shelterId}
 *
 * @property id Unique identifier of the shelter.
 * @property name Name of the shelter.
 * @property street Street address.
 * @property city City where shelter is located.
 * @property postalCode Postal code.
 * @property phone Contact phone number.
 * @property nif Tax identification number.
 * @property openingTime Opening time (e.g., "09:00:00").
 * @property closingTime Closing time (e.g., "18:00:00").
 */
data class ResShelterDto(
    val id: String,
    val name: String,
    val street: String,
    val city: String,
    val postalCode: String,
    val phone: String,
    val nif: String,
    val openingTime: String,
    val closingTime: String
)