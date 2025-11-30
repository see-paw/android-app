package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.local.dao.AnimalDao
import com.example.seepawandroid.data.local.entities.Animal
import com.example.seepawandroid.data.models.mappers.toDto
import com.example.seepawandroid.data.models.mappers.toEntity
import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.Animals.AnimalFilterDto
import com.example.seepawandroid.data.remote.dtos.animals.PagedAnimals
import com.example.seepawandroid.data.remote.dtos.animals.ResAnimalDto
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

    /**
     * Retrieves detailed information about a single animal by ID.
     *
     * Behavior:
     * - If there is **network connection**, fetches from the backend and updates local cache.
     * - If **offline** or the API fails, returns the cached animal from Room.
     * - If the animal is not in cache and there's no internet, returns an error.
     *
     * @param animalId The unique identifier of the animal.
     * @return Result containing either the animal data or an error.
     *         Success includes a boolean indicating if data came from cache (offline mode).
     */
    suspend fun getAnimalById(animalId: String): Result<Pair<ResAnimalDto, Boolean>> {
        return try {
            // Check internet connection
            val isOnline = NetworkUtils.isConnected()

            if (isOnline) {
                // Try to fetch from backend
                val response = apiService.getAnimalById(animalId)

                if (response.isSuccessful && response.body() != null) {
                    val animalDto = response.body()!!

                    // Save to cache
                    val entity = animalDto.toEntity()
                    dao.insertAnimal(entity)

                    // Return with online flag
                    Result.success(Pair(animalDto, false))
                } else {
                    // API failed, try cache
                    val cached = dao.getAnimalById(animalId)
                    if (cached != null) {
                        // Convert entity to DTO (you'll need a mapper for this)
                        val dto = cached.toDto()
                        Result.success(Pair(dto, true))
                    } else {
                        Result.failure(Exception("Animal not found"))
                    }
                }
            } else {
                // Offline mode - try cache
                val cached = dao.getAnimalById(animalId)
                if (cached != null) {
                    val dto = cached.toDto()
                    Result.success(Pair(dto, true))
                } else {
                    Result.failure(Exception("No internet connection and animal not cached"))
                }
            }
        } catch (e: Exception) {
            // On exception, try cache as fallback
            val cached = dao.getAnimalById(animalId)
            if (cached != null) {
                val dto = cached.toDto()
                Result.success(Pair(dto, true))
            } else {
                Result.failure(e)
            }
        }
    }
}
