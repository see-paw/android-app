package com.example.seepawandroid.data.remote.api.services

import com.example.seepawandroid.data.remote.dtos.PagedListDto
import com.example.seepawandroid.data.remote.dtos.animals.ResAnimalDto
import com.example.seepawandroid.data.remote.dtos.animals.ResOwnedAnimalDto
import com.example.seepawandroid.data.remote.dtos.auth.ReqLoginDto
import com.example.seepawandroid.data.remote.dtos.auth.ResLoginDto
import com.example.seepawandroid.data.remote.dtos.auth.ReqRegisterUserDto
import com.example.seepawandroid.data.remote.dtos.ownerships.ReqOwnershipRequestDto
import com.example.seepawandroid.data.remote.dtos.ownerships.ResOwnershipRequestDto
import com.example.seepawandroid.data.remote.dtos.ownerships.ResOwnershipRequestListDto
import com.example.seepawandroid.data.remote.dtos.shelter.ResShelterDto
import com.example.seepawandroid.data.remote.dtos.user.ResUserDataDto
import com.example.seepawandroid.data.remote.dtos.user.ResUserIdDto
import com.example.seepawandroid.data.remote.dtos.user.ResUserRoleDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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
     * Fetches a paginated list of animals from the backend API with optional filtering
     * and sorting criteria.
     *
     * This endpoint supports multiple query parameters that allow the client
     * to refine the search results based on species, age, size, color, sex, name,
     * shelter name, breed, sorting field, sorting order, and pagination.
     *
     * All parameters are optional unless specified otherwise.
     *
     * @param species Optional species filter (e.g., "Dog", "Cat").
     * @param age Optional age filter (in years).
     * @param size Optional size filter (e.g., "Small", "Medium", "Large").
     * @param color Optional color filter (string match).
     * @param sex Optional sex filter ("Male" or "Female").
     * @param name Optional search filter for animal name.
     * @param shelterName Optional filter for the name of the shelter.
     * @param breed Optional breed filter.
     * @param sortBy Optional sorting field (e.g., "name", "age", "size").
     * @param order Optional sorting direction ("asc" or "desc").
     * @param pageNumber The page index to request. Defaults to 1.
     *
     * @return A paginated response containing a list of animal DTOs.
     */
    @GET("api/animals")
    suspend fun getAnimals(
        @Query("species") species: String? = null,
        @Query("age") age: Int? = null,
        @Query("size") size: String? = null,
        @Query("color") color: String? = null,
        @Query("sex") sex: String? = null,
        @Query("name") name: String? = null,
        @Query("shelterName") shelterName: String? = null,
        @Query("breed") breed: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("order") order: String? = null,
        @Query("pageNumber") pageNumber: Int = 1
    ): Response<PagedListDto<ResAnimalDto>>

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
     * Fetches the authenticated user's complete data.
     * Requires valid authentication token.
     *
     * @return Response containing the user's data
     */
    @GET("api/Users/me")
    suspend fun getUserData(): Response<ResUserDataDto>

    /**
     * Fetches detailed information about a single animal by its ID.
     *
     * @param id The unique identifier of the animal.
     * @return Response containing the animal DTO.
     */
    @GET("api/Animals/{id}")
    suspend fun getAnimalById(@Path("id") id: String): Response<ResAnimalDto>

    /**
     * Fetches detailed information about a specific shelter by its ID.
     *
     * @param shelterId The unique identifier of the shelter.
     * @return Response containing shelter data.
     */
    @GET("api/Shelters/{shelterId}")
    suspend fun getShelterById(@Path("shelterId") shelterId: String): Response<ResShelterDto>

    /**
     * Fetches all ownership requests made by the authenticated user.
     *
     * Returns list format with animal images and extended information.
     * Requires authentication via bearer token.
     *
     * @return Response containing list of ownership requests with images.
     */
    @GET("api/OwnershipRequests/user-requests")
    suspend fun getUserOwnershipRequests(): Response<List<ResOwnershipRequestListDto>>

    /**
     * Creates a new ownership request for an animal.
     *
     * Requires authentication via bearer token.
     *
     * @param request The ownership request body containing animalId.
     * @return Response containing the created ownership request.
     */

    /**
     * Fetches animals owned by the authenticated user (approved ownership requests).
     *
     * Requires authentication via bearer token.
     *
     * @return Response containing list of owned animals.
     */
    @GET("api/OwnershipRequests/owned-animals")
    suspend fun getOwnedAnimals(): Response<List<ResOwnedAnimalDto>>

    @POST("api/OwnershipRequests")
    suspend fun createOwnershipRequest(@Body request: ReqOwnershipRequestDto): Response<ResOwnershipRequestDto>

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

    /**
     * Registers a new user account.
     *
     * @param registerData User registration data
     * @return Response with no body on success (200 OK)
     */
    @POST("api/Account/register")
    suspend fun register(@Body registerData: ReqRegisterUserDto): Response<Unit>

}