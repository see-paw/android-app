package com.example.seepawandroid.ui.navigation

/**
 * Centralized navigation routes for the entire application.
 *
 * Contains all screen route constants used by Navigation Compose.
 * Singleton object that centralizes all navigation routes for the SeePaw application.
 */
object NavigationRoutes {
    // ========== PUBLIC ROUTES (UNAUTHENTICATED) ==========
    /**
     * Route for the login screen.
     */
    const val LOGIN = "login"

    /**
     * Route for the registration screen.
     */
    const val REGISTER = "register"

    /**
     * Route for the public homepage.
     */
    const val HOMEPAGE = "homepage"

    /**
     * Route for the animals catalogue in guest mode (no favorite action).
     */
    const val ANIMALS_CATALOGUE_GUEST = "AnimalsCatalogueGuest"

    /**
     * Route for the animal detail page in guest mode (no ownership action), with animal ID parameter.
     */
    const val ANIMAL_DETAIL_PAGE_GUEST = "animal_detail_page_guest/{animalId}"

    /**
     * Base route for the animal detail page in guest mode without parameters.
     */
    const val ANIMAL_DETAIL_PAGE_GUEST_BASE = "animal_detail_page_guest"

    // ========== USER ROUTES (AUTHENTICATED) ==========
    /**
     * Route for the authenticated user homepage.
     */
    const val USER_HOMEPAGE = "user_homepage"

    /**
     * Route for the animals catalogue in authenticated mode (favorites available).
     */
    const val ANIMALS_CATALOGUE = "AnimalsCatalogue"

    /**
     * Route for the admin homepage.
     */
    const val ADMIN_HOME = "admin_home"

    /**
     * Route for the animal detail page in authenticated mode (ownership available), with animal ID parameter.
     */
    const val ANIMAL_DETAIL_PAGE = "animal_detail_page/{animalId}"

    /**
     * Base route for the animal detail page in authenticated mode without parameters.
     */
    const val ANIMAL_DETAIL_PAGE_BASE = "animal_detail_page"

    /**
     * Route for creating an ownership request with animal ID, name, and shelter ID parameters.
     */
    const val OWNERSHIP_REQUEST =
        "ownership_request/{animalId}?animalName={animalName}&shelterId={shelterId}"

    /**
     * Base route for the ownership request screen without parameters.
     */
    const val OWNERSHIP_REQUEST_BASE = "ownership_request"

    /**
     * Route for viewing the user's ownership requests list.
     */
    const val OWNERSHIP_LIST = "ownership_list"

    /**
     * Route for scheduling a visit with an animal.
     */
    const val SCHEDULE_VISIT = "schedule_visit"
}
