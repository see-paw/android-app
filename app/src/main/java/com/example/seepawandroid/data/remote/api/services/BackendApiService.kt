package com.example.seepawandroid.data.remote.api.services

import com.example.seepawandroid.data.remote.dtos.auth.ReqLoginDto
import com.example.seepawandroid.data.remote.dtos.auth.ResLoginDto
import com.example.seepawandroid.data.remote.dtos.user.ResUserIdDto
import com.example.seepawandroid.data.remote.dtos.user.ResUserRoleDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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
     * Fetches the authenticated user's role from the backend.
     *
     * This endpoint requires authentication. The JWT token is automatically
     * added to the request headers via AuthInterceptor.
     *
     * @return Response containing [ResUserRoleDto] with the user's role
     */
    @GET("api/Users/role")
    suspend fun getUserRole(): Response<ResUserRoleDto>

    /**
     * Fetches the authenticated user's ID from the backend.
     *
     * @return Response containing [ResUserIdDto] with the user's ID
     */
    @GET("api/Users/id")
    suspend fun getUserId(): Response<ResUserIdDto>

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