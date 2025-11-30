package com.example.seepawandroid.ui.screens.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.models.activities.Slot
import com.example.seepawandroid.data.models.mappers.toSchedule
import com.example.seepawandroid.data.repositories.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SchedulingViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<ScheduleUiState>()
    val uiState: LiveData<ScheduleUiState> = _uiState

    private var lastAnimalId: String? = null
    private var lastStartDate: LocalDate? = null

    fun loadSchedule(
        animalId: String,
        startDate: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY) ) {
        lastAnimalId = animalId
        lastStartDate = startDate

        viewModelScope.launch {
            _uiState.value = ScheduleUiState.Loading

            val result = activityRepository.getWeeklySchedule(animalId, startDate)

            _uiState.value = result.fold(
                onSuccess = { scheduleDto ->
                    val schedule = scheduleDto.toSchedule()
                    lastStartDate = schedule.weekStartDate

                    ScheduleUiState.Success(
                        schedule = schedule
                    )
                },
                onFailure = { exception ->
                    ScheduleUiState.Error(exception.message ?: "Erro ao carregar agenda")                        }
            )
        }
    }

    fun onSlotClick(slot: Slot) {
    }

    fun retry() {
        lastAnimalId?.let { animalId ->
            loadSchedule(animalId, lastStartDate?:  LocalDate.now().with(DayOfWeek.MONDAY))
        }
    }

    fun loadPrevWeek() {
        TODO("Not yet implemented")
    }

    fun loadNextWeek() {
        TODO("Not yet implemented")
    }
}