package com.example.seepawandroid.data.managers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.seepawandroid.data.models.enums.NotificationType
import com.example.seepawandroid.data.remote.api.services.NotificationService
import com.example.seepawandroid.data.remote.dtos.notifications.ResNotificationDto
import com.example.seepawandroid.data.remote.dtos.notifications.ResSignalRNotificationDto
import com.example.seepawandroid.data.repositories.NotificationRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager responsible for notification business logic.
 *
 * Handles both offline notifications (fetch from API) and real-time notifications (SignalR).
 * Processes notifications and updates relevant state managers.
 *
 * Responsibilities:
 * - Fetch offline notifications when user logs in
 * - Process real-time notifications from NotificationService
 * - Update OwnershipStateManager when ownership status changes
 * - Mark notifications as read
 * - Delete notifications
 * - Cache notifications in memory (NO Room database)
 */
@Singleton
class NotificationManager @Inject constructor(
    private val notificationService: NotificationService,
    private val notificationRepository: NotificationRepository,
    private val ownershipStateManager: OwnershipStateManager,
    private val sessionManager: SessionManager
) {

    // ========== IN-MEMORY CACHE (LiveData) ==========

    private val _notifications = MutableLiveData<List<ResNotificationDto>>(emptyList())
    val notifications: LiveData<List<ResNotificationDto>> = _notifications

    private val _ownershipApprovedEvent = MutableLiveData<OwnershipApprovedEvent?>()
    val ownershipApprovedEvent: LiveData<OwnershipApprovedEvent?> = _ownershipApprovedEvent

    private val _unreadCount = MutableLiveData(0)
    val unreadCount: LiveData<Int> = _unreadCount

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Event data for ownership approved notification.
     */
    data class OwnershipApprovedEvent(
        val notificationId: String,
        val animalId: String
    )

    init {
        // Register callback for real-time notifications from SignalR
        notificationService.setOnNotificationReceivedCallback { signalRNotification ->
            processRealtimeNotification(signalRNotification)
        }
    }

    // ========== REAL-TIME NOTIFICATIONS ==========

    /**
     * Clears the ownership approved event after consumption.
     */
    fun clearOwnershipApprovedEvent() {
        _ownershipApprovedEvent.postValue(null)
    }

    /**
     * Connects to SignalR for real-time notifications.
     * Should be called after successful login.
     */
    fun connectRealtime() {
        val token = sessionManager.getAuthToken()
        if (token != null) {
            notificationService.connect(token)
        }
    }

    /**
     * Disconnects from SignalR.
     * Should be called on logout.
     */
    fun disconnectRealtime() {
        notificationService.disconnect()
    }

    /**
     * Processes a real-time notification received from SignalR.
     *
     * @param signalRNotification The notification DTO from SignalR.
     */
    private fun processRealtimeNotification(signalRNotification: ResSignalRNotificationDto) {
        //debug
        android.util.Log.d("NotificationManager", ">>> processRealtimeNotification CALLED: ${signalRNotification.id}")

        // Convert to standard DTO (adds isRead = false)
        val notification = signalRNotification.toResNotificationDto()

        // Add to cache
        addNotificationToCache(notification)

        // Process notification logic
        processNotification(notification)
    }

    /**
     * Processes a notification and triggers appropriate actions.
     *
     * @param notification The notification to process.
     */
    private fun processNotification(notification: ResNotificationDto) {
        val notificationType = NotificationType.fromString(notification.type) ?: return

        when (notificationType) {
            NotificationType.OWNERSHIP_REQUEST_APPROVED -> {
                // Emit event for UI to show dialog
                if (notification.animalId != null) {
                    _ownershipApprovedEvent.postValue(
                        OwnershipApprovedEvent(
                            notificationId = notification.id,
                            animalId = notification.animalId
                        )
                    )
                }

                // Trigger refetch of ownership requests
                kotlinx.coroutines.GlobalScope.launch {
                    ownershipStateManager.fetchAndUpdateState()
                }
            }
            NotificationType.OWNERSHIP_REQUEST_ANALYZING,
            NotificationType.OWNERSHIP_REQUEST_REJECTED -> {
                kotlinx.coroutines.GlobalScope.launch {
                    ownershipStateManager.fetchAndUpdateState()
                }
            }
            else -> {
                // Other notification types don't require ownership refetch
            }
        }
    }

    // ========== OFFLINE NOTIFICATIONS ==========

    /**
     * Fetches offline notifications (notifications that arrived while user was offline).
     * Should be called after login.
     *
     * @return Result indicating success or failure.
     */
    suspend fun fetchOfflineNotifications(): Result<Unit> {
        _isLoading.postValue(true)

        return try {
            val result = notificationRepository.getNotifications(unreadOnly = null)

            if (result.isSuccess) {
                val notifications = result.getOrNull()!!
                _notifications.postValue(notifications)
                updateUnreadCount(notifications)
                Result.success(Unit)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.postValue(false)
        }
    }

    /**
     * Marks a notification as read.
     *
     * @param notificationId The ID of the notification to mark as read.
     */
    suspend fun markNotificationAsRead(notificationId: String): Result<Unit> {
        val result = notificationRepository.markAsRead(notificationId)

        if (result.isSuccess) {
            // Update cache
            val updatedList = _notifications.value?.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            } ?: emptyList()

            _notifications.postValue(updatedList)
            updateUnreadCount(updatedList)
        }

        return result
    }

    /**
     * Deletes a notification.
     *
     * @param notificationId The ID of the notification to delete.
     */
    suspend fun deleteNotification(notificationId: String): Result<Unit> {
        val result = notificationRepository.deleteNotification(notificationId)

        if (result.isSuccess) {
            // Remove from cache
            val updatedList = _notifications.value?.filter { it.id != notificationId } ?: emptyList()
            _notifications.postValue(updatedList)
            updateUnreadCount(updatedList)
        }

        return result
    }

    // ========== CACHE HELPERS ==========

    /**
     * Adds a new notification to the in-memory cache.
     * Used when receiving real-time notifications.
     */
    private fun addNotificationToCache(notification: ResNotificationDto) {
        val currentList = _notifications.value.orEmpty().toMutableList()

        //debug
        android.util.Log.d("NotificationManager", ">>> addNotificationToCache - before: ${currentList.size}")

        // Add at the beginning (most recent first)
        currentList.add(0, notification)

        _notifications.postValue(currentList)

        //debug
        android.util.Log.d("NotificationManager", ">>> addNotificationToCache - after: ${currentList.size}")

        updateUnreadCount(currentList)
    }

    /**
     * Updates the unread notification count.
     */
    private fun updateUnreadCount(notifications: List<ResNotificationDto>) {
        val count = notifications.count { !it.isRead }
        _unreadCount.postValue(count)
    }

    // ========== STATE SYNCHRONIZATION ==========

    /**
     * Called on login to synchronize state.
     * Fetches ownership requests and offline notifications.
     */
    suspend fun initializeOnLogin(): Result<Unit> {
        return try {
            // 1. Fetch ownership requests
            ownershipStateManager.fetchAndUpdateState()

            // 2. Fetch offline notifications
            fetchOfflineNotifications()

            // 3. Connect to real-time
            connectRealtime()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Called on logout to clean up.
     */
    fun cleanupOnLogout() {
        disconnectRealtime()
        _notifications.postValue(emptyList())
        _unreadCount.postValue(0)
    }

    // ========== PUBLIC HELPERS ==========

    /**
     * Gets current notifications synchronously.
     */
    fun getCurrentNotifications(): List<ResNotificationDto> {
        return _notifications.value.orEmpty()
    }

    /**
     * Gets current unread count synchronously.
     */
    fun getCurrentUnreadCount(): Int {
        return _unreadCount.value ?: 0
    }
}