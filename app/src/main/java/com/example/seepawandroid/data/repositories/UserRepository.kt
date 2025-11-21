package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.remote.api.services.BackendApiService
import javax.inject.Inject
import com.example.seepawandroid.data.providers.RetrofitInstance
import com.example.seepawandroid.data.remote.api.services.BackendApiService
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
     * Fetches the authenticated user's role from the backend.
     *
     * This method requires a valid authentication token. The token is automatically
     * included in the request via AuthInterceptor.
     *
     * @return Result containing the user's role as a String on success,
     *         or an exception on failure
     *
     * @throws Exception if the network request fails or the response is unsuccessful
     */
    suspend fun fetchUserRole(): Result<String> {
        return try {
            val response = apiService.getUserRole()

            if (response.isSuccessful && response.body() != null) {
                val role = response.body()!!.role
                Result.success(role)
            } else {
                Result.failure(Exception("Failed to fetch user role: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches the authenticated user's ID from the backend.
     *
     * @return Result containing the user's ID as a String on success,
     *         or an exception on failure
     */
    suspend fun fetchUserId(): Result<String> {
        return try {
            val response = apiService.getUserId()

            if (response.isSuccessful && response.body() != null) {
                val userId = response.body()!!.userId
                Result.success(userId)
            } else {
                Result.failure(Exception("Failed to fetch user ID: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}