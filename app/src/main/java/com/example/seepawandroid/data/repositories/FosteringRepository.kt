package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.models.domain.ActiveFostering
import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.fosterings.ReqAddFosteringDto
import com.example.seepawandroid.data.remote.dtos.fosterings.ResActiveFosteringDto
import com.example.seepawandroid.data.remote.dtos.fosterings.ResActiveFosteringIdDto
import com.example.seepawandroid.data.remote.dtos.fosterings.ResCancelFosteringDto
import javax.inject.Inject

/**
 * Repository responsible for managing fostering operations.
 *
 * Handles communication with the fosterings API endpoints.
 */
class FosteringRepository @Inject constructor(
    private val apiService: BackendApiService
) {

    /**
     * Retrieves active fosterings with complete information (including IDs).
     *
     * Combines data from /api/fosterings and /api/fosterings/ids by index.
     * Assumes both endpoints return fosterings in the same order.
     *
     * @return Result containing list of ActiveFostering with IDs.
     */
    suspend fun getActiveFosterings(): Result<List<ResActiveFosteringDto>> {
        return try {
            val response = apiService.getActiveFosterings()

            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else if (response.code() == 404) {
                val errorBody = response.errorBody()?.string()
                if (errorBody?.contains("No active fostering records found", ignoreCase = true) == true) {
                    Result.success(emptyList())
                } else {
                    Result.failure(Exception("Endpoint not found: ${response.code()}"))
                }
            } else {
                Result.failure(Exception("Failed to load fosterings: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches the IDs of active fosterings for the authenticated user.
     *
     * @return Result containing list of fostering IDs or error.
     */
    suspend fun getActiveFosteringIds(): Result<List<ResActiveFosteringIdDto>> {
        return try {
            val response = apiService.getActiveFosteringIds()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 404) {
                val errorBody = response.errorBody()?.string()
                if (errorBody?.contains("No active fostering records found", ignoreCase = true) == true) {
                    Result.success(emptyList())
                } else {
                    Result.failure(Exception("Endpoint not found: ${response.code()}"))
                }
            } else {
                Result.failure(Exception("Failed to fetch fostering IDs: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Creates a new fostering for an animal.
     *
     * @param animalId The ID of the animal to foster.
     * @param monthValue The monthly contribution amount in euros.
     * @return Result containing the created fostering or error.
     */
    suspend fun createFostering(animalId: String, monthValue: Double): Result<ResActiveFosteringDto> {
        return try {
            val request = ReqAddFosteringDto(monthValue = monthValue)
            val response = apiService.createFostering(animalId, request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Animal não encontrado"
                    409 -> "Não é possível apadrinhar este animal"
                    422 -> "O valor mensal excede o custo do animal"
                    else -> "Erro ao criar apadrinhamento: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cancels an active fostering.
     *
     * @param fosteringId The ID of the fostering to cancel.
     * @return Result containing the cancelled fostering details or error.
     */
    suspend fun cancelFostering(fosteringId: String): Result<ResCancelFosteringDto> {
        return try {
            val response = apiService.cancelFostering(fosteringId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Apadrinhamento não encontrado"
                    403 -> "Não tem permissão para cancelar este apadrinhamento"
                    else -> "Erro ao cancelar apadrinhamento: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}