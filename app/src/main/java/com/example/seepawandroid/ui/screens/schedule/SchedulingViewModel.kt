package com.example.seepawandroid.ui.screens.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.models.mappers.toReqCreateOwnershipDto
import com.example.seepawandroid.data.models.mappers.toSchedule
import com.example.seepawandroid.data.models.schedule.AvailableSlot
import com.example.seepawandroid.data.repositories.ActivityRepository
import com.example.seepawandroid.data.repositories.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SchedulingViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<ScheduleUiState>()
    val uiState: LiveData<ScheduleUiState> = _uiState

    private val _modalUiState = MutableLiveData<ModalUiState>(ModalUiState.Hidden)
    val modalUiState: LiveData<ModalUiState> = _modalUiState

    fun loadSchedule(
        animalId: String,
        startDate: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY) ) {
        viewModelScope.launch {
            _uiState.value = ScheduleUiState.Loading

            val result = scheduleRepository.getWeeklySchedule(animalId, startDate)

            _uiState.value = result.fold(
                onSuccess = { scheduleDto ->
                    val schedule = scheduleDto.toSchedule()

                    ScheduleUiState.Success(
                        schedule = schedule
                    )
                },
                onFailure = { exception ->
                    ScheduleUiState.Error(
                        animalId = animalId,
                        startDate = startDate,
                        message = exception.message ?: "Erro ao carregar agenda")                        }
            )
        }
    }

    fun onSlotClick(slot: AvailableSlot, animalId: String, animalName: String) {
        _modalUiState.value = ModalUiState.Confirm(slot, animalId, animalName)
    }

    fun confirmSlot(slot: AvailableSlot, animalId: String, animalName: String) {
        val dto = slot.toReqCreateOwnershipDto(animalId)

        viewModelScope.launch {
            _modalUiState.value = ModalUiState.Loading

            val result = activityRepository.createOwnershipActivity(dto)

            _modalUiState.value = result.fold(
                onSuccess = {
                    val currentState = uiState.value

                    if (currentState is ScheduleUiState.Success) {
                        val id = currentState.schedule.animalId
                        val date = currentState.schedule.weekStartDate

                        loadSchedule(id, date)
                    }

                    ModalUiState.Hidden
                },
                onFailure = { exception ->
                    ModalUiState.Error(
                        slot = slot,
                        animalId = animalId,
                        animalName = animalName,
                        message = exception.message ?: "Erro ao confirmar atividade com $animalName"
                    )
                }
            )
        }
    }

    fun cancelSlot() {
        _modalUiState.value = ModalUiState.Hidden
    }

    fun loadPrevWeek(animalId: String, currWeekStartDate: LocalDate) {
        val prevWeekStartDate = currWeekStartDate.minusWeeks(1)
        loadSchedule(animalId, prevWeekStartDate)
    }

    fun loadNextWeek(animalId: String, currWeekStartDate: LocalDate) {
        val nextWeekStartDate = currWeekStartDate.plusWeeks(1)
        loadSchedule(animalId, nextWeekStartDate)
    }
}