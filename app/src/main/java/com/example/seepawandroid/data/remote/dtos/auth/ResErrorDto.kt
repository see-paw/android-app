package com.example.seepawandroid.data.remote.dtos.auth

/**
 * Data Transfer Object for error responses from the backend API.
 *
 * Represents the standard error format returned by the .NET backend
 * using ASP.NET Core Identity.
 *
 * @property status HTTP status code
 * @property message Human-readable error message
 * @property details Additional error details
 */
data class ResErrorDto(
    val status: Int?,
    val message: String?,
    val details: String?
)