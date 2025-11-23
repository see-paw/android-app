package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.local.dao.AnimalDao
import com.example.seepawandroid.data.local.entities.Animal
import com.example.seepawandroid.data.models.PagedAnimals
import com.example.seepawandroid.data.models.mappers.toEntity
import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.Animals.AnimalFilterDto
import com.example.seepawandroid.utils.NetworkUtils
import javax.inject.Inject

/**
 * Repository responsible for managing animal data from both local and remote sources.
 *
 * It handles:
 * - Fetching animals from the backend API with pagination and filters
 * - Converting DTOs to Room entities
 * - Updating the local cache
 * - Returning cached data when offline or when the backend request fails
 */
class AnimalRepository @Inject constructor(
    private val dao: AnimalDao,
    private val apiService: BackendApiService
) {

    /**
     * Retrieves a paginated list of animals, applying filters and sorting options.
     *
     * Behavior:
     * - If there is **no network connection**, returns **all cached animals** in a single page.
     * - If the API request fails, also falls back to the local cache.
     * - If successful, data from the backend is mapped to Room entities and persisted.
     *
     * @param filters Optional filter criteria for species, size, sex, breed, etc.
     * @param sortBy Optional sorting field (e.g., "name", "age").
     * @param order Sort direction ("asc" or "desc").
     * @param pageNumber The page number to request from the backend.
     *
     * @return A [PagedAnimals] object representing paginated animal results.
     */
    suspend fun getAnimalsPaged(
        filters: AnimalFilterDto?,
        sortBy: String?,
        order: String?,
        pageNumber: Int
    ): PagedAnimals {
        return try {
            // Offline: return cached results as a single page
            if (!NetworkUtils.isConnected()) {
                val cached = dao.getAll()
                return PagedAnimals(
                    items = cached,
                    currentPage = 1,
                    totalPages = 1,
                    totalCount = cached.size
                )
            }

            // Attempt remote call
            val response = apiService.getAnimals(
                species = filters?.species,
                age = filters?.age,
                size = filters?.size,
                sex = filters?.sex,
                name = filters?.name,
                shelterName = filters?.shelterName,
                breed = filters?.breed,
                sortBy = sortBy,
                order = order,
                pageNumber = pageNumber
            )

            // If API error, fallback to cache
            if (!response.isSuccessful) {
                val cached = dao.getAll()
                return PagedAnimals(
                    items = cached,
                    currentPage = 1,
                    totalPages = 1,
                    totalCount = cached.size
                )
            }

            val body = response.body()
                ?: throw IllegalStateException("Empty response from backend")

            // Convert DTOs to entities
            val entities: List<Animal> = body.items.map { it.toEntity() }

            // Update cache
            dao.insertAll(entities)

            // Calculate total pages based on totalCount and pageSize
            val totalPages = if (body.pageSize == 0 || body.totalCount == 0) {
                1
            } else {
                (body.totalCount + body.pageSize - 1) / body.pageSize
            }

            PagedAnimals(
                items = entities,
                currentPage = body.currentPage,
                totalPages = totalPages,
                totalCount = body.totalCount
            )
        } catch (e: Exception) {
            // On exception, fallback to cache
            val cached = dao.getAll()
            PagedAnimals(
                items = cached,
                currentPage = 1,
                totalPages = 1,
                totalCount = cached.size
            )
        }
    }
}
