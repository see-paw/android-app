package com.example.seepawandroid.data.managers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.seepawandroid.data.models.enums.OwnershipStatus
import com.example.seepawandroid.data.remote.dtos.ownerships.ResOwnershipRequestDto
import com.example.seepawandroid.data.repositories.OwnershipRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton manager that holds the cached state of user's ownership requests.
 *
 * Provides a single source of truth for ownership data across the entire app.
 * All ViewModels should observe the LiveData exposed by this manager.
 *
 * Responsibilities:
 * - In-memory cache of ownership requests (LiveData)
 * - State updates from various sources (API, SignalR, local actions)
 * - Synchronization with backend via OwnershipRepository
 */
@Singleton
class OwnershipStateManager @Inject constructor(
    private val ownershipRepository: OwnershipRepository
) {

    // ========== STATE (LIVEDATA CACHE) ==========

    /**
     * Internal mutable LiveData for ownership requests cache.
     */
    private val _ownershipRequests = MutableLiveData<List<ResOwnershipRequestDto>>(emptyList())

    /**
     * Public read-only LiveData exposing user's ownership requests.
     * All ViewModels should observe this for reactive updates.
     */
    val ownershipRequests: LiveData<List<ResOwnershipRequestDto>> = _ownershipRequests

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
            val result = ownershipRepository.getUserOwnershipRequests()

            if (result.isSuccess) {
                _ownershipRequests.postValue(result.getOrNull()!!)
                Result.success(Unit)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } finally {
            _isLoading.postValue(false)
        }
    }

    /**
     * Adds a new ownership request to the cache.
     * Called after successfully creating a request.
     *
     * @param request The newly created ownership request.
     */
    fun addOwnershipRequest(request: ResOwnershipRequestDto) {
        val currentList = _ownershipRequests.value.orEmpty()
        val updatedList = currentList + request
        _ownershipRequests.postValue(updatedList)
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
    fun getOwnershipRequestForAnimal(animalId: String): ResOwnershipRequestDto? {
        return _ownershipRequests.value?.firstOrNull { it.animalId == animalId }
    }

    /**
     * Clears all cached ownership requests.
     * Should be called on logout.
     */
    fun clearState() {
        _ownershipRequests.postValue(emptyList())
    }

    /**
     * Gets the current cached list synchronously (for immediate checks).
     * Use LiveData observation for reactive updates in ViewModels.
     *
     * @return Current list of ownership requests.
     */
    fun getCurrentRequests(): List<ResOwnershipRequestDto> {
        return _ownershipRequests.value.orEmpty()
    }
}