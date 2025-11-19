package com.example.seepawandroid.data.remote.dtos.auth

/**
 * Data Transfer Object for login response.
 *
 * Contains authentication token and user information received from the backend
 * after successful login.
 *
 * @property accessToken JWT token used for authenticating subsequent API requests
 * @property expiresIn seconds until the token expires
 * @property tokenType type of the token (e.g., "Bearer")
 * @property refreshToken token used to refresh the access token if it expires
 */
data class ResLoginDto(
    val accessToken: String,
    val expiresIn: Int,
    val tokenType: String,
    val refreshToken: String
)