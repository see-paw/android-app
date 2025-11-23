package com.example.seepawandroid.data.remote.dtos.user

/**
 * Data Transfer Object representing the authenticated user's complete data.
 *
 * This DTO is returned by the /api/Users/me endpoint and contains all
 * relevant information about the currently logged-in user.
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