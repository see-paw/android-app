package com.example.seepawandroid.services


import com.example.seepawandroid.data.remote.dtos.auth.ReqLoginDto
import com.example.seepawandroid.data.remote.dtos.auth.ResLoginDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit service interface for backend API communication.
 *
 * Defines all endpoints available in the SeePaw backend API.
 * Retrofit automatically generates the implementation at compile time.
 *
 * All methods are suspend functions to support Kotlin coroutines for asynchronous operations.
 */
interface BackendApiService {
    /**
     * Authenticates a user with email and password.
     *
     * Endpoint: POST /api/login
     *
     * @param credentials User's email and password
     * @return Response containing authentication token and user info on success
     */
    @POST("api/login")
    suspend fun login(@Body credentials: ReqLoginDto): Response<ResLoginDto>
}