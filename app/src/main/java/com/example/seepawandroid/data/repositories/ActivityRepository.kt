package com.example.seepawandroid.data.repositories

import android.util.Log
import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.activities.ResScheduleResponseDto
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ActivityRepository @Inject constructor(
    private val apiService: BackendApiService
) {
    suspend fun getWeeklySchedule(
        animalId: String,
        startDate: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY)
    ) : Result<ResScheduleResponseDto>{
        return try {
            val startDateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

            Log.i("REPOSITORY", "$animalId:::::$startDateString")

            val response = apiService.getWeekAnimalSchedule(animalId, startDateString)

            Log.i("REPOSITORY", "$response")

            if (response.isSuccessful && response.body() != null) {
                return Result.success(response.body()!!)
            } else {
                return Result.failure(Exception("Failed to fetch schedule: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}