package com.example.seepawandroid.data.managers

import com.example.seepawandroid.data.models.enums.OwnershipStatus
import com.example.seepawandroid.data.remote.api.services.NotificationService
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
 */
@Singleton
class NotificationManager @Inject constructor(
    private val notificationService: NotificationService,
    private val ownershipStateManager: OwnershipStateManager,
    private val sessionManager: SessionManager
) {

    init {
        // Register callback for real-time notifications from SignalR
        notificationService.setOnNotificationReceivedCallback { type, payload ->
            processRealtimeNotification(type, payload)
        }
    }

    // ========== REAL-TIME NOTIFICATIONS ==========

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
     * @param type The notification type (e.g., "OwnershipStatusChanged").
     * @param payload The notification data.
     */
    private fun processRealtimeNotification(type: String, payload: Map<String, Any>) {
        when (type) {
            "OwnershipStatusChanged" -> handleOwnershipStatusChanged(payload)
            "NEW_OWNERSHIP_REQUEST" -> handleNewOwnershipRequest(payload)
            "OWNERSHIP_REQUEST_APPROVED" -> handleOwnershipApproved(payload)
            "OWNERSHIP_REQUEST_ANALYZING" -> handleOwnershipAnalyzing(payload)
            "OWNERSHIP_REQUEST_REJECTED" -> handleOwnershipRejected(payload)
            // Add more notification types as needed
            else -> {
                // Unknown notification type - log or ignore
            }
        }
    }

    /**
     * Handles ownership status change notification.
     */
    private fun handleOwnershipStatusChanged(payload: Map<String, Any>) {
        val requestId = payload["requestId"] as? String ?: return
        val newStatusStr = payload["newStatus"] as? String ?: return

        // Convert string to enum
        val newStatus = try {
            OwnershipStatus.valueOf(newStatusStr)
        } catch (e: IllegalArgumentException) {
            return // Invalid status
        }

        // Update state manager
        ownershipStateManager.updateOwnershipRequestStatus(requestId, newStatus)
    }

    /**
     * Handles new ownership request notification (admin).
     * For regular users, this might just trigger a refresh.
     */
    private fun handleNewOwnershipRequest(payload: Map<String, Any>) {
        // Could trigger a refresh or show a notification
        // For now, we don't need to do anything for users
    }

    /**
     * Handles ownership request approved notification.
     */
    private fun handleOwnershipApproved(payload: Map<String, Any>) {
        val requestId = payload["requestId"] as? String ?: return
        ownershipStateManager.updateOwnershipRequestStatus(requestId, OwnershipStatus.Approved)
    }

    /**
     * Handles ownership request analyzing notification.
     */
    private fun handleOwnershipAnalyzing(payload: Map<String, Any>) {
        val requestId = payload["requestId"] as? String ?: return
        ownershipStateManager.updateOwnershipRequestStatus(requestId, OwnershipStatus.Analysing)
    }

    /**
     * Handles ownership request rejected notification.
     */
    private fun handleOwnershipRejected(payload: Map<String, Any>) {
        val requestId = payload["requestId"] as? String ?: return
        ownershipStateManager.updateOwnershipRequestStatus(requestId, OwnershipStatus.Rejected)
    }

    // ========== OFFLINE NOTIFICATIONS ==========

    /**
     * Fetches offline notifications (notifications that arrived while user was offline).
     * Should be called after login if SignalR wasn't connected.
     *
     * TODO: Implement when backend notifications endpoint is ready.
     *
     * @return Result indicating success or failure.
     */
    suspend fun fetchOfflineNotifications(): Result<Unit> {
        // TODO: Implement when backend has GET /api/notifications endpoint
        // return try {
        //     val response = apiService.getUnreadNotifications()
        //     if (response.isSuccessful && response.body() != null) {
        //         val notifications = response.body()!!
        //         notifications.forEach { notification ->
        //             processOfflineNotification(notification)
        //         }
        //         Result.success(Unit)
        //     } else {
        //         Result.failure(Exception("Failed to fetch notifications"))
        //     }
        // } catch (e: Exception) {
        //     Result.failure(e)
        // }

        return Result.success(Unit) // Placeholder
    }

    /**
     * Processes an offline notification.
     * Similar to real-time processing but with notification object.
     *
     * TODO: Implement when backend notifications model is defined.
     */
    private fun processOfflineNotification(notification: Any) {
        // TODO: Parse notification and call appropriate handler
        // Example:
        // when (notification.type) {
        //     NotificationType.OWNERSHIP_REQUEST_APPROVED -> {
        //         ownershipStateManager.updateOwnershipRequestStatus(
        //             notification.ownershipRequestId,
        //             OwnershipStatus.Approved
        //         )
        //     }
        //     // ... other types
        // }
    }

    /**
     * Marks a notification as read.
     *
     * TODO: Implement when backend endpoint is ready.
     *
     * @param notificationId The ID of the notification to mark as read.
     */
    suspend fun markNotificationAsRead(notificationId: String): Result<Unit> {
        // TODO: Implement PUT/PATCH to backend
        // return try {
        //     val response = apiService.markNotificationAsRead(notificationId)
        //     if (response.isSuccessful) {
        //         Result.success(Unit)
        //     } else {
        //         Result.failure(Exception("Failed to mark notification as read"))
        //     }
        // } catch (e: Exception) {
        //     Result.failure(e)
        // }

        return Result.success(Unit) // Placeholder
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
        // Clear any notification cache if we add one later
    }
}