package com.example.seepawandroid.data.models.enums

/**
 * Enum representing user roles in the SeePaw application.
 *
 * Defines the different permission levels and access types for users.
 * Each role corresponds to a role string returned by the backend API.
 */
enum class UserRole(val value: String) {
    /**
     * Regular user with basic permissions.
     * Can browse animals, create adoption requests, and manage favorites.
     */
    USER("User"),

    /**
     * Shelter administrator (Admin Centro de Acolhimento Animal).
     * Can manage animals, approve/reject adoption requests, and manage shelter activities.
     */
    ADMIN_CAA("AdminCAA"),

    /**
     * Platform administrator with full system access.
     * Can manage all shelters, users, and system-wide settings.
     */
    PLATFORM_ADMIN("PlatformAdmin");

    companion object {
        /**
         * Converts a role string from the backend to a UserRole enum.
         *
         * @param value Role string from API (e.g., "User", "AdminCAA")
         * @return Corresponding UserRole enum, defaults to USER if not found
         */
        fun fromString(value: String): UserRole {
            return entries.find { it.value == value } ?: USER
        }
    }
}