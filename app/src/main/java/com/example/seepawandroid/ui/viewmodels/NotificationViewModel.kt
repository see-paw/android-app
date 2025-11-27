package pt.ipp.estg.seepawandroid.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.managers.NotificationManager
import com.example.seepawandroid.data.managers.SessionManager
import com.example.seepawandroid.data.remote.dtos.notifications.ResNotificationDto
import com.example.seepawandroid.data.repositories.AnimalRepository
import com.example.seepawandroid.ui.viewmodels.NotificationDropdownUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pt.ipp.estg.seepawandroid.ui.models.OwnershipApprovedDialogData
import javax.inject.Inject

/**
 * ViewModel for notification dropdown.
 *
 * Observes NotificationManager and provides paginated notifications with max 5 per page.
 * Also handles ownership approved dialog display.
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationManager: NotificationManager,
    private val sessionManager: SessionManager,
    private val animalRepository: AnimalRepository
) : ViewModel() {

    companion object {
        private const val NOTIFICATIONS_PER_PAGE = 5
    }

    // ========== STATE ==========

    private val _currentPage = MutableLiveData(0)
    val currentPage: LiveData<Int> = _currentPage

    private val _uiState = MediatorLiveData<NotificationDropdownUiState>()
    val uiState: LiveData<NotificationDropdownUiState> = _uiState

    private val _showOwnershipApprovedDialog = MutableLiveData<OwnershipApprovedDialogData?>(null)
    val showOwnershipApprovedDialog: LiveData<OwnershipApprovedDialogData?> = _showOwnershipApprovedDialog

    init {
        // Observe notifications from NotificationManager
        _uiState.addSource(notificationManager.notifications) { notifications ->
            updateUiState(notifications)
        }

        // Observe unread count
        _uiState.addSource(notificationManager.unreadCount) { _ ->
            updateUiState(notificationManager.getCurrentNotifications())
        }

        // Observe loading state
        _uiState.addSource(notificationManager.isLoading) { isLoading ->
            if (isLoading) {
                _uiState.value = NotificationDropdownUiState.Loading
            } else {
                updateUiState(notificationManager.getCurrentNotifications())
            }
        }

        // Observe current page changes
        _uiState.addSource(_currentPage) { _ ->
            updateUiState(notificationManager.getCurrentNotifications())
        }

        // Observe ownership approved events
        _uiState.addSource(notificationManager.ownershipApprovedEvent) { event ->
            if (event != null) {
                handleOwnershipApprovedEvent(event)
                notificationManager.clearOwnershipApprovedEvent()
            }
        }
    }

    // ========== UI STATE CALCULATION ==========

    private fun updateUiState(allNotifications: List<ResNotificationDto>) {
        // Don't override loading state
        if (notificationManager.isLoading.value == true) {
            return
        }

        when {
            allNotifications.isEmpty() -> {
                _uiState.value = NotificationDropdownUiState.Empty
            }
            else -> {
                val currentPageIndex = _currentPage.value ?: 0
                val totalPages = (allNotifications.size + NOTIFICATIONS_PER_PAGE - 1) / NOTIFICATIONS_PER_PAGE

                // Ensure current page is valid
                val validPage = currentPageIndex.coerceIn(0, (totalPages - 1).coerceAtLeast(0))

                // Get notifications for current page
                val startIndex = validPage * NOTIFICATIONS_PER_PAGE
                val endIndex = (startIndex + NOTIFICATIONS_PER_PAGE).coerceAtMost(allNotifications.size)
                val pageNotifications = allNotifications.subList(startIndex, endIndex)

                _uiState.value = NotificationDropdownUiState.Success(
                    notifications = pageNotifications,
                    currentPage = validPage,
                    totalPages = totalPages,
                    unreadCount = notificationManager.getCurrentUnreadCount()
                )
            }
        }
    }

    // ========== ACTIONS ==========

    /**
     * Navigates to the next page of notifications.
     */
    fun nextPage() {
        val state = _uiState.value
        if (state is NotificationDropdownUiState.Success) {
            val nextPage = (state.currentPage + 1).coerceAtMost(state.totalPages - 1)
            _currentPage.value = nextPage
        }
    }

    /**
     * Navigates to the previous page of notifications.
     */
    fun previousPage() {
        val state = _uiState.value
        if (state is NotificationDropdownUiState.Success) {
            val prevPage = (state.currentPage - 1).coerceAtLeast(0)
            _currentPage.value = prevPage
        }
    }

    /**
     * Marks a notification as read.
     *
     * @param notificationId The ID of the notification.
     */
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationManager.markNotificationAsRead(notificationId)
        }
    }

    /**
     * Deletes a notification.
     *
     * @param notificationId The ID of the notification.
     */
    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            val result = notificationManager.deleteNotification(notificationId)

            // If we deleted the last notification on current page, go to previous page
            if (result.isSuccess) {
                val state = _uiState.value
                if (state is NotificationDropdownUiState.Success) {
                    val allNotifications = notificationManager.getCurrentNotifications()
                    val totalPages = (allNotifications.size + NOTIFICATIONS_PER_PAGE - 1) / NOTIFICATIONS_PER_PAGE

                    if (state.currentPage >= totalPages && state.currentPage > 0) {
                        _currentPage.value = state.currentPage - 1
                    }
                }
            }
        }
    }

    /**
     * Refreshes notifications from the backend.
     */
    fun refresh() {
        viewModelScope.launch {
            notificationManager.fetchOfflineNotifications()
        }
    }

    /**
     * Resets to first page.
     */
    fun resetToFirstPage() {
        _currentPage.value = 0
    }

    // ========== OWNERSHIP APPROVED DIALOG ==========

    /**
     * Handles ownership approved event by fetching animal data and showing dialog.
     */
    private fun handleOwnershipApprovedEvent(event: NotificationManager.OwnershipApprovedEvent) {
        viewModelScope.launch {
            try {
                // Get user name from SessionManager
                val userName = sessionManager.getUserName() ?: "Utilizador"

                // Fetch animal data
                val animalResult = animalRepository.getAnimalById(event.animalId)

                if (animalResult.isSuccess) {
                    val (animalDto, _) = animalResult.getOrNull()!!
                    val principalImage = animalDto.images?.firstOrNull { it.isPrincipal }

                    _showOwnershipApprovedDialog.postValue(
                        OwnershipApprovedDialogData(
                            notificationId = event.notificationId,
                            userName = userName,
                            animalName = animalDto.name,
                            animalImageUrl = principalImage?.url ?: ""
                        )
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("NotificationViewModel", "Error showing ownership dialog", e)
            }
        }
    }

    /**
     * Dismisses the ownership approved dialog and marks notification as read.
     */
    fun dismissOwnershipApprovedDialog() {
        val dialogData = _showOwnershipApprovedDialog.value
        if (dialogData != null) {
            markAsRead(dialogData.notificationId)
        }
        _showOwnershipApprovedDialog.value = null
    }
}