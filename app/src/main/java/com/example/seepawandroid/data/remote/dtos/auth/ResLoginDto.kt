package com.example.seepawandroid.data.remote.dtos.auth

/**
 * Data Transfer Object for login response.
 *
 * Contains authentication token and user information received from the backend
 * after successful login.
 *
 * @property accessToken JWT token used for authenticating subsequent API requests
 * @property tokenExpiration timestamp indicating when the token expires
 * @property userId Unique identifier of the authenticated user
 * @property role User's role (e.g., "User" or "AdminCAA")
 */
data class ResLoginDto(
    val accessToken: String,
    val tokenExpiration: String,
    val userId: String,
    val role: String
)