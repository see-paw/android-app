package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.schedule.ResScheduleResponseDto
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Repository for managing animal schedules.
 */
class ScheduleRepository @Inject constructor(
    private val apiService: BackendApiService
) {
    /**
     * Fetches the weekly schedule for a specific animal.
     *
     * @param animalId The unique identifier of the animal.
     * @param startDate The start date of the week, defaults to current Monday.
     * @return Result containing the schedule response or an error.
     */
    suspend fun getWeeklySchedule(
        animalId: String,
        startDate: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY)
    ) : Result<ResScheduleResponseDto>{
        return try {
            val startDateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

            val response = apiService.getWeekAnimalSchedule(animalId, startDateString)

            return if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch schedule: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}