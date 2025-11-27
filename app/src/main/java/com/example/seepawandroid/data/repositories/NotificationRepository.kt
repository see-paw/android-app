package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.notifications.ResNotificationDto
import javax.inject.Inject

/**
 * Repository for notification-related API operations.
 *
 * Pure repository pattern - no caching, no LiveData, just API calls.
 * All business logic is handled by NotificationManager.
 *
 * Methods return Result<T> for proper error handling.
 */
class NotificationRepository @Inject constructor(
    private val apiService: BackendApiService
) {

    /**
     * Fetches notifications from the backend.
     *
     * @param unreadOnly If true, fetches only unread notifications.
     * @return Result containing list of notifications or error.
     */
    suspend fun getNotifications(unreadOnly: Boolean? = null): Result<List<ResNotificationDto>> {
        return try {
            val response = apiService.getNotifications(unreadOnly)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch notifications: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marks a notification as read.
     *
     * @param notificationId The ID of the notification to mark as read.
     * @return Result indicating success or failure.
     */
    suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            val response = apiService.markNotificationAsRead(notificationId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark notification as read: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a notification.
     *
     * @param notificationId The ID of the notification to delete.
     * @return Result indicating success or failure.
     */
    suspend fun deleteNotification(notificationId: String): Result<Unit> {
        return try {
            val response = apiService.deleteNotification(notificationId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete notification: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}