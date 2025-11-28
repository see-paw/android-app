package com.example.seepawandroid.data.managers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.seepawandroid.data.models.enums.OwnershipStatus
import com.example.seepawandroid.data.remote.dtos.animals.ResOwnedAnimalDto
import com.example.seepawandroid.data.remote.dtos.ownerships.ResOwnershipRequestDto
import com.example.seepawandroid.data.remote.dtos.ownerships.ResOwnershipRequestListDto
import com.example.seepawandroid.data.repositories.OwnershipRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton manager that holds the cached state of user's ownership requests.
 *
 * Uses ResOwnershipRequestListDto (with images) for cache since it contains
 * all necessary information for display.
 *
 * When a new request is created (ResOwnershipRequestDto), we fetch the list
 * again to get the full data with images.
 */
@Singleton
class OwnershipStateManager @Inject constructor(
    private val ownershipRepository: OwnershipRepository
) {

    // ========== STATE (LIVEDATA CACHE) ==========

    /**
     * Internal mutable LiveData for ownership requests cache.
     * Uses ListDto because it has images and full info.
     */
    private val _ownershipRequests = MutableLiveData<List<ResOwnershipRequestListDto>>(emptyList())

    /**
     * Public read-only LiveData exposing user's ownership requests.
     * All ViewModels should observe this for reactive updates.
     */
    val ownershipRequests: LiveData<List<ResOwnershipRequestListDto>> = _ownershipRequests

    /**
     * Internal mutable LiveData for owned animals cache.
     */
    private val _ownedAnimals = MutableLiveData<List<ResOwnedAnimalDto>>(emptyList())

    /**
     * Public read-only LiveData exposing user's owned animals.
     * All ViewModels should observe this for reactive updates.
     */
    val ownedAnimals: LiveData<List<ResOwnedAnimalDto>> = _ownedAnimals

    // ========== LOADING STATE ==========

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // ========== PUBLIC METHODS ==========

    /**
     * Fetches ownership requests from backend and updates cache.
     * Should be called on login and when manually refreshing.
     *
     * @return Result indicating success or failure.
     */
    suspend fun fetchAndUpdateState(): Result<Unit> {
        _isLoading.postValue(true)

        return try {
            // Fetch active requests
            val requestsResult = ownershipRepository.getUserOwnershipRequests()

            // Fetch owned animals
            val ownedResult = ownershipRepository.getOwnedAnimals()

            if (requestsResult.isSuccess && ownedResult.isSuccess) {
                _ownershipRequests.postValue(requestsResult.getOrNull()!!)
                _ownedAnimals.postValue(ownedResult.getOrNull()!!)
                Result.success(Unit)
            } else {
                // Return first failure found
                val error = requestsResult.exceptionOrNull()
                    ?: ownedResult.exceptionOrNull()
                    ?: Exception("Unknown error")
                Result.failure(error)
            }
        } finally {
            _isLoading.postValue(false)
        }
    }

    /**
     * Called after successfully creating a new ownership request.
     *
     * Since POST returns ResOwnershipRequestDto (without image), we immediately
     * fetch the full list to get the complete data with images.
     *
     * @param basicRequest The newly created ownership request (basic DTO).
     */
    suspend fun addOwnershipRequest(basicRequest: ResOwnershipRequestDto) {
        // Immediately fetch updated list with images
        fetchAndUpdateState()
    }

    /**
     * Updates the status of an ownership request in cache.
     * Will be used by NotificationManager when receiving real-time updates.
     *
     * @param requestId The ID of the ownership request.
     * @param newStatus The new status.
     */
    fun updateOwnershipRequestStatus(requestId: String, newStatus: OwnershipStatus) {
        val currentList = _ownershipRequests.value.orEmpty()
        val updatedList = currentList.map { request ->
            if (request.id == requestId) {
                request.copy(status = newStatus)
            } else {
                request
            }
        }
        _ownershipRequests.postValue(updatedList)
    }

    /**
     * Removes an ownership request from cache.
     * Can be used if a request is deleted or cancelled.
     *
     * @param requestId The ID of the ownership request to remove.
     */
    fun removeOwnershipRequest(requestId: String) {
        val currentList = _ownershipRequests.value.orEmpty()
        val updatedList = currentList.filter { it.id != requestId }
        _ownershipRequests.postValue(updatedList)
    }

    /**
     * Checks if user already has an ownership request for a specific animal.
     * Uses cached data for instant response (no API call).
     *
     * @param animalId The ID of the animal to check.
     * @return True if user has an existing request, false otherwise.
     */
    fun hasOwnershipRequestForAnimal(animalId: String): Boolean {
        return _ownershipRequests.value?.any { it.animalId == animalId } == true
    }

    /**
     * Gets the ownership request for a specific animal, if it exists.
     * Uses cached data (no API call).
     *
     * @param animalId The ID of the animal.
     * @return The ownership request or null if not found.
     */
    fun getOwnershipRequestForAnimal(animalId: String): ResOwnershipRequestListDto? {
        return _ownershipRequests.value?.firstOrNull { it.animalId == animalId }
    }

    /**
     * Clears all cached ownership requests.
     * Should be called on logout.
     */
    fun clearState() {
        _ownershipRequests.postValue(emptyList())
        _ownedAnimals.postValue(emptyList())
    }

    /**
     * Gets the current cached list synchronously (for immediate checks).
     * Use LiveData observation for reactive updates in ViewModels.
     *
     * @return Current list of ownership requests.
     */
    fun getCurrentRequests(): List<ResOwnershipRequestListDto> {
        return _ownershipRequests.value.orEmpty()
    }
}