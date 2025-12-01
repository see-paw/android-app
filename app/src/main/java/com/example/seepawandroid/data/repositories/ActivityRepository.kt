package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.activities.ReqCreateOwnershipActivityDto
import javax.inject.Inject

class ActivityRepository @Inject constructor(
    private val apiService: BackendApiService
) {
    suspend fun createOwnershipActivity(
        dto: ReqCreateOwnershipActivityDto) : Result<Unit> {
        return try {
            val response = apiService.createOwnershipActivity(dto)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create ownership activity: HTTP ${response.code()}"))
            }
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}