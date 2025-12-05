package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.animals.ResOwnedAnimalDto
import com.example.seepawandroid.data.remote.dtos.ownerships.ReqOwnershipRequestDto
import com.example.seepawandroid.data.remote.dtos.ownerships.ResOwnershipRequestDto
import com.example.seepawandroid.data.remote.dtos.ownerships.ResOwnershipRequestListDto
import javax.inject.Inject

/**
 * Repository for managing ownership requests.
 *
 * Handles:
 * - Creating new ownership requests
 * - Fetching user's ownership requests
 */
class OwnershipRepository @Inject constructor(
    private val apiService: BackendApiService
) {

    /**
     * Creates a new ownership request for an animal.
     *
     * @param animalId The ID of the animal to request ownership for.
     * @return Result containing the created ownership request or an error.
     */
    suspend fun createOwnershipRequest(animalId: String): Result<ResOwnershipRequestDto> {
        return try {
            val request = ReqOwnershipRequestDto(animalId = animalId)
            val response = apiService.createOwnershipRequest(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create ownership request: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches all ownership requests made by the authenticated user.
     *
     * Returns extended DTO with animal images and state.
     *
     * @return Result containing list of ownership requests (with images) or an error.
     */
    suspend fun getUserOwnershipRequests(): Result<List<ResOwnershipRequestListDto>> {
        return try {
            val response = apiService.getUserOwnershipRequests()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch ownership requests: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches animals owned by the authenticated user.
     * These are ownership requests that were approved.
     *
     * @return Result containing list of owned animals or an error.
     */
    suspend fun getOwnedAnimals(): Result<List<ResOwnedAnimalDto>> {
        return try {
            val response = apiService.getOwnedAnimals()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch owned animals: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}