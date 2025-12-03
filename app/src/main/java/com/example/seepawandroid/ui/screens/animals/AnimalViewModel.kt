package com.example.seepawandroid.ui.screens.animals.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.remote.dtos.animals.AnimalFilterDto
import com.example.seepawandroid.data.repositories.AnimalRepository
import com.example.seepawandroid.data.repositories.FavoriteRepository
import com.example.seepawandroid.ui.screens.animals.AnimalCatalogueUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for:
 * - Loading animals from the repository
 * - Managing filters, sorting, and search queries
 * - Handling pagination
 * - Exposing UI state for the catalogue screen
 *
 * It retrieves paginated animal data from the repository and converts
 * results into UI-friendly state objects.
 */
@HiltViewModel
class AnimalViewModel @Inject constructor(
    private val repository: AnimalRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<AnimalCatalogueUiState>()
    val uiState: LiveData<AnimalCatalogueUiState> = _uiState

    private val _breeds = MutableLiveData<List<String>>()
    val breeds: LiveData<List<String>> = _breeds

    private var searchQuery: String = ""
    val currentSearchQuery: String
        get() = searchQuery

    private var filters: AnimalFilterDto? = null
    private var sortBy: String? = null
    private var order: String? = null

    var currentPage: Int = 1
        private set
    var totalPages: Int = 1
        private set

    private var favoriteIds: MutableSet<String> = mutableSetOf()

    /**
     * Loads animals for the given page, applying filters, sorting,
     * and search query if provided.
     *
     * Updates the UI state accordingly.
     */
    fun loadAnimals(page: Int = 1) {
        viewModelScope.launch {
            _uiState.value = AnimalCatalogueUiState.Loading

            try {
                val paged = repository.getAnimalsPaged(
                    filters = buildEffectiveFilters(),
                    sortBy = sortBy,
                    order = order,
                    pageNumber = page
                )

                currentPage = paged.currentPage
                totalPages = paged.totalPages

                if (paged.items.isEmpty()) {
                    _uiState.value = AnimalCatalogueUiState.Empty
                } else {
                    // Extract unique breed list from current page
                    val breedList = paged.items
                        .mapNotNull { it.breedName }
                        .filter { it.isNotBlank() }
                        .distinct()
                        .sorted()

                    _breeds.value = breedList

                    _uiState.value = AnimalCatalogueUiState.Success(
                        animals = paged.items,
                        currentPage = paged.currentPage,
                        totalPages = paged.totalPages,
                        totalCount = paged.totalCount,
                        favoriteIds = favoriteIds.toSet()
                    )
                }

            } catch (e: Exception) {
                _uiState.value = AnimalCatalogueUiState.Error(
                    e.message ?: "Error loading animals"
                )
            }
        }
    }

    /**
     * Applies a search query to the catalogue and reloads results.
     */
    fun search(query: String) {
        if (query == searchQuery) return

        searchQuery = query

        val baseFilters = filters ?: AnimalFilterDto()
        filters = baseFilters.copy(name = if (query.isBlank()) null else query)

        loadAnimals(1)
    }

    /**
     * Applies user-selected filters and reloads animals.
     */
    fun applyFilters(newFilters: AnimalFilterDto?) {

        if (newFilters == null) {
            // Reset only filters — keep search & sorting
            filters = if (searchQuery.isNotBlank()) {
                AnimalFilterDto(name = searchQuery)
            } else null

            currentPage = 1
            loadAnimals(1)
            return
        }

        filters = newFilters.copy(
            name = if (searchQuery.isBlank()) null else searchQuery
        )

        currentPage = 1
        loadAnimals(1)
    }



    /**
     * Applies sorting and reloads the first page.
     */
    fun applySorting(sort: String?, direction: String?) {
        sortBy = sort
        order = direction

        currentPage = 1
        loadAnimals(1)
    }

    /** Navigates to the next page of animals, if available. */
    fun nextPage() {
        if (currentPage < totalPages) loadAnimals(currentPage + 1)
    }

    /** Navigates to the previous page of animals, if available. */
    fun previousPage() {
        if (currentPage > 1) loadAnimals(currentPage - 1)
    }

    /** Navigates to a specific page within the valid range. */
    fun goToPage(page: Int) {
        if (page in 1..totalPages) loadAnimals(page)
    }

    /**
     * Internal helper that merges filters with the search query.
     */
    private fun buildEffectiveFilters(): AnimalFilterDto? {
        val base = filters ?: AnimalFilterDto()

        val updated = base.copy(
            name = if (searchQuery.isBlank()) base.name else searchQuery
        )

        return if (updated == AnimalFilterDto()) null else updated
    }

    /**
     * Utility method used by the UI to determine if any filters
     * (species, size, sex, breed, shelter or name/search query)
     * are currently active.
     *
     * This does not perform any backend logic — it simply checks
     * whether the internal filter state represents a non-empty
     * filter set. The Screen uses this information to decide
     * whether an "empty results" message should appear due to
     * filters/search, instead of the generic empty catalogue state.
     *
     * @return True if at least one filter field is active; false otherwise.
     */
    fun currentFiltersNotEmpty(): Boolean {
        return filters != null && filters != AnimalFilterDto()
    }

    /**
     * Loads the user's favorites from the backend.
     * Should be called when the user is authenticated.
     */
    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val result = favoriteRepository.getFavorites(pageNumber = 1, pageSize = 1000)
                result.onSuccess { pagedList ->
                    favoriteIds.clear()
                    favoriteIds.addAll(pagedList.items.map { it.id })

                    // Refresh UI state with updated favorites
                    val currentState = _uiState.value
                    if (currentState is AnimalCatalogueUiState.Success) {
                        _uiState.value = currentState.copy(favoriteIds = favoriteIds.toSet())
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AnimalViewModel", "Error loading favorites", e)
            }
        }
    }

    /**
     * Toggles the favorite status of an animal.
     * If the animal is currently a favorite, it will be removed.
     * If it's not a favorite, it will be added.
     *
     * @param animalId The ID of the animal to toggle
     */
    fun toggleFavorite(animalId: String) {
        viewModelScope.launch {
            try {
                val isFavorite = favoriteIds.contains(animalId)

                val result = if (isFavorite) {
                    favoriteRepository.removeFavorite(animalId)
                } else {
                    favoriteRepository.addFavorite(animalId)
                }

                result.onSuccess {
                    // Update local state
                    if (isFavorite) {
                        favoriteIds.remove(animalId)
                    } else {
                        favoriteIds.add(animalId)
                    }

                    // Refresh UI state with updated favorites
                    val currentState = _uiState.value
                    if (currentState is AnimalCatalogueUiState.Success) {
                        _uiState.value = currentState.copy(favoriteIds = favoriteIds.toSet())
                    }

                    android.util.Log.d("AnimalViewModel", "Favorite toggled successfully for animal: $animalId")
                }.onFailure { error ->
                    android.util.Log.e("AnimalViewModel", "Error toggling favorite", error)
                }
            } catch (e: Exception) {
                android.util.Log.e("AnimalViewModel", "Error toggling favorite", e)
            }
        }
    }
}
