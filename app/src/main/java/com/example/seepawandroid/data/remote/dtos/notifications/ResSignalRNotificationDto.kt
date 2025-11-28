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
    @SerializedName("id")
    val id: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("animalId")
    val animalId: String?,

    @SerializedName("ownershipRequestId")
    val ownershipRequestId: String?,

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