package pt.ipp.estg.seepaw.ui.navigation

/**
 * Singleton object that centralizes all navigation routes for the SeePaw application.
 */
object NavigationRoutes {

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


}
