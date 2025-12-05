package com.example.seepawandroid.data.remote.dtos.notifications

import com.google.gson.annotations.SerializedName

/**
 * DTO for notifications received via SignalR real-time connection.
 *
 * This is separate from ResNotificationDto because:
 * - SignalR doesn't send 'isRead' field (real-time notifications are always unread)
 * - Field names match backend's anonymous object serialization
 *
 * Received from: SignalR "ReceiveNotification" event
 */
data class ResSignalRNotificationDto(
    /**
     * The unique identifier of the notification.
     */
    @SerializedName("id")
    val id: String,

    /**
     * The type of notification.
     */
    @SerializedName("type")
    val type: String,

    /**
     * The notification message content.
     */
    @SerializedName("message")
    val message: String,

    /**
     * The ID of the related animal, if applicable.
     */
    @SerializedName("animalId")
    val animalId: String?,

    /**
     * The ID of the related ownership request, if applicable.
     */
    @SerializedName("ownershipRequestId")
    val ownershipRequestId: String?,

    /**
     * The timestamp when the notification was created.
     */
    @SerializedName("createdAt")
    val createdAt: String
) {
    /**
     * Converts SignalR notification to standard notification DTO.
     * Real-time notifications are always unread.
     */
    fun toResNotificationDto(): ResNotificationDto {
        return ResNotificationDto(
            id = id,
            type = type,
            message = message,
            animalId = animalId,
            ownershipRequestId = ownershipRequestId,
            isRead = false,
            createdAt = createdAt
        )
    }
}