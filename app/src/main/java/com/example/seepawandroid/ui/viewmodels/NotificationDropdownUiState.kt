package com.example.seepawandroid.ui.viewmodels

import com.example.seepawandroid.data.remote.dtos.notifications.ResNotificationDto

/**
 * UI state for the notification dropdown.
 *
 * Represents all possible states of the notification dropdown component.
 */
sealed class NotificationDropdownUiState {
    /**
     * Loading notifications from the backend.
     */
    object Loading : NotificationDropdownUiState()

    /**
     * Successfully loaded notifications.
     *
     * @property notifications List of notifications to display (max 5 per page).
     * @property currentPage Current page index (0-based).
     * @property totalPages Total number of pages available.
     * @property unreadCount Total number of unread notifications.
     */
    data class Success(
        val notifications: List<ResNotificationDto>,
        val currentPage: Int,
        val totalPages: Int,
        val unreadCount: Int
    ) : NotificationDropdownUiState()

    /**
     * No notifications available (empty state).
     */
    object Empty : NotificationDropdownUiState()

    /**
     * Error loading notifications.
     *
     * @property message Error message to display.
     */
    data class Error(val message: String) : NotificationDropdownUiState()

    /**
     * User is offline - cannot load notifications.
     */
    object Offline : NotificationDropdownUiState()
}