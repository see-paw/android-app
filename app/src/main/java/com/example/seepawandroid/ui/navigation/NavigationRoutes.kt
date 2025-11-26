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

    const val REGISTER = "register"

    const val HOMEPAGE = "homepage"

    //Animals catalogue (guest mode – no favorite action)
    const val ANIMALS_CATALOGUE_GUEST = "AnimalsCatalogueGuest"

    //Animal detail page (guest mode – no ownership action)
    const val ANIMAL_DETAIL_PAGE_GUEST = "animal_detail_page_guest/{animalId}"
    const val ANIMAL_DETAIL_PAGE_GUEST_BASE = "animal_detail_page_guest"

    // ========== USER ROUTES (AUTHENTICATED) ==========
    const val USER_HOMEPAGE = "user_homepage"

    //Animals catalogue (authenticated mode – favorites available)
    const val ANIMALS_CATALOGUE = "AnimalsCatalogue"

    // Admin routes (authentication required)
    const val ADMIN_HOME = "admin_home"

    //Animal detail page (authenticated mode – ownership available)
    const val ANIMAL_DETAIL_PAGE = "animal_detail_page/{animalId}"
    const val ANIMAL_DETAIL_PAGE_BASE = "animal_detail_page"

    //Ownership request (authenticated mode - request animal adoption)
    // USER ROUTES
    const val OWNERSHIP_REQUEST = "ownership_request/{animalId}?animalName={animalName}&shelterId={shelterId}"
    const val OWNERSHIP_REQUEST_BASE = "ownership_request"
}
