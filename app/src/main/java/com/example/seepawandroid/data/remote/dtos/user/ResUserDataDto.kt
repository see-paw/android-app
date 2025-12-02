package com.example.seepawandroid.data.remote.dtos.user

/**
 * Data Transfer Object representing the authenticated user's complete data.
 *
 * This DTO is returned by the /api/Users/me endpoint and contains all
 * relevant information about the currently logged-in user.
 *
 * @property userId The unique identifier of the user.
 * @property email The user's email address.
 * @property name The user's full name.
 * @property role The user's role in the system.
 * @property shelterId The ID of the shelter the user is associated with, if applicable.
 * @property birthDate The user's date of birth.
 * @property street The user's street address.
 * @property city The user's city of residence.
 * @property postalCode The user's postal code.
 * @property phoneNumber The user's phone number.
 */
data class ResUserDataDto(
    val userId: String,
    val email: String,
    val name: String,
    val role: String,
    val shelterId: String?,
    val birthDate: String,
    val street: String,
    val city: String,
    val postalCode: String,
    val phoneNumber: String
)