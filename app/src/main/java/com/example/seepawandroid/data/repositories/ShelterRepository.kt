package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.shelter.ResShelterDto
import javax.inject.Inject

/**
 * Repository for shelter-related data operations.
 *
 * Handles fetching shelter information from the backend API.
 */
class ShelterRepository @Inject constructor(
    private val apiService: BackendApiService
) {

    /**
     * Fetches detailed information about a specific shelter.
     *
     * @param shelterId The unique identifier of the shelter.
     * @return Result containing shelter data or an error.
     */
    suspend fun getShelterById(shelterId: String): Result<ResShelterDto> {
        return try {
            val response = apiService.getShelterById(shelterId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch shelter data: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}