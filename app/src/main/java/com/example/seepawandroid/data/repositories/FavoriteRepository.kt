package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.PagedListDto
import com.example.seepawandroid.data.remote.dtos.favorites.ResGetFavoritesDto
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    private val apiService: BackendApiService
) {
    suspend fun getFavorites(
        pageNumber: Int = 1,
        pageSize: Int = 10
    ): Result<PagedListDto<ResGetFavoritesDto>> {
        return try {
            val response = apiService.getFavorites(pageNumber, pageSize)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (ex: IOException) {
            Result.failure(ex)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun addFavorite(
        animalId: String
    ): Result<Unit> {
        return try {
            val response = apiService.addFavorite(animalId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (ex: IOException) {
            Result.failure(ex)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun removeFavorite(
        animalId: String
    ): Result<Unit> {
        return try {
            val response = apiService.removeFavorite(animalId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (ex: IOException) {
            Result.failure(ex)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}