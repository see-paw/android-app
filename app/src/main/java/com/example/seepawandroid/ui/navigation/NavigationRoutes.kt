package com.example.seepawandroid.ui.navigation

/**
 * Centralized navigation routes for the entire application.
 *
 * Contains all screen route constants used by Navigation Compose.
 * Singleton object that centralizes all navigation routes for the SeePaw application.
 */
object NavigationRoutes {
    // ========== PUBLIC ROUTES (UNAUTHENTICATED) ==========
    const val LOGIN = "login"

    const val HOMEPAGE = "homepage"

    /**
     * Animal catalogue (guest mode – no favorite/adoption actions)
     */
    const val ANIMALS_CATALOGUE_GUEST = "AnimalsCatalogueGuest"


    // ========== USER ROUTES (AUTHENTICATED) ==========
    const val USER_HOME = "user_home"


    /**
     * Animal catalogue (authenticated mode – favorites available)
     */
    const val ANIMALS_CATALOGUE = "AnimalsCatalogue"


    // Admin routes (authentication required)
    const val ADMIN_HOME = "admin_home"
}
