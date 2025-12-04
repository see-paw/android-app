package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.activities.ReqCreateOwnershipActivityDto
import com.example.seepawandroid.data.repositories.exceptions.ActivityException
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

/**
 * Repository for managing animal activities.
 */
class ActivityRepository @Inject constructor(
    private val apiService: BackendApiService
) {
    /**
     * Creates a new ownership activity for a specific time slot.
     *
     * @param dto The request containing animal ID and activity time details.
     * @return Result indicating success or specific activity exception.
     * @throws ActivityException.SlotAlreadyBookedException if slot is already booked.
     * @throws ActivityException.InvalidSlotException if slot data is invalid.
     * @throws ActivityException.ServerException if server error occurs.
     * @throws ActivityException.NetworkException if network error occurs.
     */
    suspend fun createOwnershipActivity(
        dto: ReqCreateOwnershipActivityDto) : Result<Unit> {
        return try {
            val response = apiService.createOwnershipActivity(dto)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)

                val exception = when (response.code()) {
                    400 -> ActivityException.InvalidSlotException(
                        errorMessage ?: "Dados inválidos"
                    )
                    409 -> ActivityException.SlotAlreadyBookedException()
                    in 500..599 -> ActivityException.ServerException(response.code())
                    else -> ActivityException.UnknownException(
                        response.code(),
                        errorBody
                    )
                }

                Result.failure(exception)
            }
        } catch (ex: IOException) {
            Result.failure(ActivityException.NetworkException(ex))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null

        return try {
            val json = JSONObject(errorBody)

            // Check for validation errors first (ASP.NET format)
            if (json.has("errors")) {
                val errors = json.getJSONObject("errors")
                val errorMessages = mutableListOf<String>()

                errors.keys().forEach { key ->
                    val fieldErrors = errors.getJSONArray(key)
                    for (i in 0 until fieldErrors.length()) {
                        errorMessages.add(fieldErrors.getString(i))
                    }
                }

                if (errorMessages.isNotEmpty()) {
                    return errorMessages.joinToString("\n")
                }
            }

            // Fallback to standard error fields
            json.optString("message")
                .takeIf { it.isNotBlank() }
                ?: json.optString("error")
                .takeIf { it.isNotBlank() }
                ?: json.optString("title")
                .takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }
}