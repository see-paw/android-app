package com.example.seepawandroid.data.remote.api.services

import com.example.seepawandroid.data.remote.dtos.PagedListDto
import com.example.seepawandroid.data.remote.dtos.Animals.ResAnimalDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BackendApiService {

    @GET("animals")
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
}
