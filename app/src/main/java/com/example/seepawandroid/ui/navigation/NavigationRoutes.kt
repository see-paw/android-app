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
     * Animals catalogue (guest mode – no favorite action)
     */
    const val ANIMALS_CATALOGUE_GUEST = "AnimalsCatalogueGuest"


    /**
     * Animal detail page (guest mode – no ownership action)
     */
    const val ANIMAL_DETAIL_PAGE_GUEST = "animal_detail_page_guest"

    fun animalDetailPageGuest(animalId: String) = "$ANIMAL_DETAIL_PAGE_GUEST/$animalId"


    // ========== USER ROUTES (AUTHENTICATED) ==========
    const val USER_HOME = "user_home"


    /**
     * Animals catalogue (authenticated mode – favorites available)
     */
    const val ANIMALS_CATALOGUE = "AnimalsCatalogue"


    // Admin routes (authentication required)
    const val ADMIN_HOME = "admin_home"

    /**
     * Animal detail page (authenticated mode – ownership available)
     */
    const val ANIMAL_DETAIL_PAGE = "animal_detail_page"

    fun animalDetailPage(animalId: String) = "$ANIMAL_DETAIL_PAGE/$animalId"
}
