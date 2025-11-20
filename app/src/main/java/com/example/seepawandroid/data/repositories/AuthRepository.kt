package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.providers.SessionManager
import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.auth.ReqLoginDto
import com.example.seepawandroid.data.remote.dtos.auth.ResLoginDto
import javax.inject.Inject

/**
 * Repository responsible for authentication operations.
 *
 * Abstracts the logic for communicating with the authentication API endpoints.
 * Handles token storage and provides a clean interface for ViewModels.
 */
class AuthRepository @Inject constructor(
    private val apiService: BackendApiService
) {

    /**
     * Authenticates a user with email and password.
     *
     * On success, stores the JWT token in SessionManager for future API requests.
     *
     * @param email User's email address
     * @param password User's password
     * @return Result object containing login response or error
     */
    suspend fun login(email: String, password: String): Result<ResLoginDto> {
        return try {
            val response = apiService.login(ReqLoginDto(email, password))

            if (response.isSuccessful && response.body() != null) {
                val loginData = response.body()!!

                // Calculate expiration time from expiresIn (seconds)
                val expirationTime = java.time.Instant.now()
                    .plusSeconds(loginData.expiresIn.toLong())
                    .toString()

                SessionManager.saveAuthToken(loginData.accessToken, expirationTime)
                Result.success(loginData)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}