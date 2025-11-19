package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.providers.RetrofitInstance
import com.example.seepawandroid.data.providers.SessionManager
import com.example.seepawandroid.data.remote.dtos.auth.ReqLoginDto
import com.example.seepawandroid.data.remote.dtos.auth.ResLoginDto
import com.example.seepawandroid.services.BackendApiService

/**
 * Repository responsible for authentication operations.
 *
 * Abstracts the logic for communicating with the authentication API endpoints.
 * Handles token storage and provides a clean interface for ViewModels.
 */
class AuthRepository {

    private val apiService: BackendApiService =
        RetrofitInstance.retrofit.create(BackendApiService::class.java)

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

                SessionManager.saveAuthToken(loginData.accessToken, loginData.tokenExpiration)
                Result.success(loginData)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}