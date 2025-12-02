package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.schedule.ResScheduleResponseDto
import retrofit2.HttpException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ScheduleRepository @Inject constructor(
    private val apiService: BackendApiService
) {
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
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}