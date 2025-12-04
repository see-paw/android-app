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
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the scheduling screen.
 *
 * @param scheduleRepository The repository for schedule-related operations.
 * @param activityRepository The repository for activity-related operations.
 */
@HiltViewModel
class SchedulingViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<ScheduleUiState>()
    /**
     * The current state of the UI.
     */
    val uiState: LiveData<ScheduleUiState> = _uiState

    private val _modalUiState = MutableLiveData<ModalUiState>(ModalUiState.Hidden)
    /**
     * The current state of the modal dialog.
     */
    val modalUiState: LiveData<ModalUiState> = _modalUiState

    /**
     * Loads the schedule for the given animal and start date.
     *
     * @param animalId The ID of the animal.
     * @param startDate The start date of the week.
     */
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

    /**
     * Called when a slot is clicked.
     *
     * @param slot The clicked slot.
     * @param animalId The ID of the animal.
     * @param animalName The name of the animal.
     */
    fun onSlotClick(slot: AvailableSlot, animalId: String, animalName: String) {
        _modalUiState.value = ModalUiState.Confirm(slot, animalId, animalName)
    }

    /**
     * Confirms the selected slot.
     *
     * @param slot The slot to confirm.
     * @param animalId The ID of the animal.
     * @param animalName The name of the animal.
     */
    fun confirmSlot(slot: AvailableSlot, animalId: String, animalName: String) {
        val dto = slot.toReqCreateOwnershipDto(animalId)

        val currentState = uiState.value
        val scheduleAnimalId = (currentState as? ScheduleUiState.Success)?.schedule?.animalId
        val scheduleWeekStart = (currentState as? ScheduleUiState.Success)?.schedule?.weekStartDate

        viewModelScope.launch {
            _modalUiState.value = ModalUiState.Loading

            val result = activityRepository.createOwnershipActivity(dto)

            _modalUiState.value = result.fold(
                onSuccess = {
                    if (scheduleAnimalId != null && scheduleWeekStart != null) {
                        loadSchedule(scheduleAnimalId, scheduleWeekStart)
                    }

                    // Format date and time for success message
                    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
                    val formattedDate = slot.start.format(dateFormatter)
                    val formattedTime = "${slot.start.format(timeFormatter)} - ${slot.end.format(timeFormatter)}"

                    ModalUiState.Success(
                        animalName = animalName,
                        date = formattedDate,
                        time = formattedTime
                    )
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

    /**
     * Cancels the slot selection.
     */
    fun cancelSlot() {
        _modalUiState.value = ModalUiState.Hidden
    }

    /**
     * Loads the previous week's schedule.
     *
     * @param animalId The ID of the animal.
     * @param currWeekStartDate The start date of the current week.
     */
    fun loadPrevWeek(animalId: String, currWeekStartDate: LocalDate) {
        val prevWeekStartDate = currWeekStartDate.minusWeeks(1)
        val currentMonday = LocalDate.now().with(DayOfWeek.MONDAY)

        // Prevent navigating to weeks before the current week
        if (prevWeekStartDate.isBefore(currentMonday)) {
            return
        }

        loadSchedule(animalId, prevWeekStartDate)
    }

    /**
     * Loads the next week's schedule.
     *
     * @param animalId The ID of the animal.
     * @param currWeekStartDate The start date of the current week.
     */
    fun loadNextWeek(animalId: String, currWeekStartDate: LocalDate) {
        val nextWeekStartDate = currWeekStartDate.plusWeeks(1)
        loadSchedule(animalId, nextWeekStartDate)
    }

    /**
     * Checks if it is possible to navigate to the previous week.
     *
     * @param currWeekStartDate The start date of the current week.
     * @return True if it is possible to navigate to the previous week, false otherwise.
     */
    fun canNavigateToPreviousWeek(currWeekStartDate: LocalDate): Boolean {
        val currentMonday = LocalDate.now().with(DayOfWeek.MONDAY)
        val prevWeekStartDate = currWeekStartDate.minusWeeks(1)
        return !prevWeekStartDate.isBefore(currentMonday)
    }
}