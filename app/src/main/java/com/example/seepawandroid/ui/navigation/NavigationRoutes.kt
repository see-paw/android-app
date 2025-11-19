package com.example.seepawandroid.ui.navigation

package pt.ipp.estg.seepaw.ui.navigation

/**
 * Centralized navigation routes for the entire application.
 *
 * Contains all screen route constants used by Navigation Compose.
 * Singleton object that centralizes all navigation routes for the SeePaw application.
 */
object NavigationRoutes {
    // Public routes (no authentication required)
    const val LOGIN = "login"

    // User routes (authentication required)
    const val USER_HOME = "user_home"
    // ========== PUBLIC ROUTES (UNAUTHENTICATED) ==========



    /**
     * Animal catalogue (guest mode – no favorite/adoption actions)
     */
    const val ANIMALS_CATALOGUE_GUEST = "AnimalsCatalogueGuest"



    // ========== USER ROUTES (AUTHENTICATED) ==========



    /**
     * Animal catalogue (authenticated mode – favorites available)
     */
    const val ANIMALS_CATALOGUE = "AnimalsCatalogue"


    // Admin routes (authentication required)
    const val ADMIN_HOME = "admin_home"
}
