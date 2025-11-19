package com.example.seepawandroid.data.remote.dtos.auth

/**
 * Data Transfer Object for login request.
 *
 * Represents the credentials sent to the backend API for authentication.
 *
 * @property email User's email address
 * @property password User's password in plain text (transmitted over HTTPS)
 */
data class ReqLoginDto(
    val email: String,
    val password: String
)