package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.user.ResUserDataDto
import javax.inject.Inject


/**
 * Repository for user-related data operations.
 *
 * Handles fetching user information from the backend API,
 * such as user role, profile data, and preferences.
 */
class UserRepository @Inject constructor(
    private val apiService: BackendApiService
){
    /**
     * Fetches the authenticated user's complete data from the backend.
     *
     * This method requires a valid authentication token. The token is automatically
     * included in the request via AuthInterceptor.
     *
     * @return Result containing ResUserDataDto on success, or an exception on failure
     * @throws Exception if the network request fails or the response is unsuccessful
     */
    suspend fun fetchUserData(): Result<ResUserDataDto> {
        return try {
            val response = apiService.getUserData()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch user data: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}