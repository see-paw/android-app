package com.example.seepawandroid.ui.screens.animals.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.remote.dtos.Animals.AnimalFilterDto
import com.example.seepawandroid.data.repositories.AnimalRepository
import com.example.seepawandroid.ui.screens.Animals.AnimalCatalogueUiState
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
    private val repository: AnimalRepository
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
                        totalCount = paged.totalCount
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
        filters = (newFilters ?: AnimalFilterDto()).copy(
            name = if (searchQuery.isBlank()) null else searchQuery
        )
        loadAnimals(1)
    }

    /**
     * Applies sorting and reloads the first page.
     */
    fun applySorting(sort: String?, direction: String?) {
        sortBy = sort
        order = direction
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
}
