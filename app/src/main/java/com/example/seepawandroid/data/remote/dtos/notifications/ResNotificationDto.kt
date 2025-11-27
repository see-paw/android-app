package com.example.seepawandroid.data.remote.dtos.notifications

/**
 * Response DTO for notifications received from the backend.
 *
 * Received from:
 * - GET /api/Notifications
 * - GET /api/Notifications?unreadOnly=true
 * - SignalR "ReceiveNotification" event
 *
 * @property id Unique identifier of the notification.
 * @property type Notification type as string (e.g., "OWNERSHIP_REQUEST_APPROVED").
 * @property message The notification message content.
 * @property animalId Optional reference to related animal.
 * @property ownershipRequestId Optional reference to related ownership request.
 * @property isRead Whether the notification has been read by the user.
 * @property createdAt ISO timestamp when the notification was created.
 */
data class ResNotificationDto(
    val id: String,
    val type: String,
    val message: String,
    val animalId: String?,
    val ownershipRequestId: String?,
    val isRead: Boolean,
    val createdAt: String
)