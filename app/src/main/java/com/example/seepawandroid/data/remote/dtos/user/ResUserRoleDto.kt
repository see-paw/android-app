package com.example.seepawandroid.data.remote.dtos.user

/**
 * Data Transfer Object representing the user role response from the backend API.
 *
 * This DTO is used when fetching the authenticated user's role via the /api/Users/role endpoint.
 *
 * @property role The user's role (e.g., "User", "AdminCAA", "PlatformAdmin")
 */
data class ResUserRoleDto(
    val role: String
)