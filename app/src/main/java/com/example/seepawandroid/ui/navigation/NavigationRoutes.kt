package com.example.seepawandroid.ui.navigation

/**
 * Centralized navigation routes for the entire application.
 *
 * Contains all screen route constants used by Navigation Compose.
 */
object NavigationRoutes {
    // Public routes (no authentication required)
    const val LOGIN = "login"

    // User routes (authentication required)
    const val USER_HOME = "user_home"

    // Admin routes (authentication required)
    const val ADMIN_HOME = "admin_home"
}