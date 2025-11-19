package com.example.seepawandroid.data.remote.dtos.user

/**
 * Data Transfer Object representing the user ID response from the backend API.
 *
 * @property userId The authenticated user's unique identifier
 */
data class ResUserIdDto(
    val userId: String
)