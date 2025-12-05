package com.example.seepawandroid.data.models.enums

/**
 * Enum representing notification types received from the backend.
 *
 * Only includes notification types relevant for regular Users.
 * Admin-specific notifications are not included since the Android app
 * only supports User sessions.
 */
enum class NotificationType {
    /**
     * Notification sent when an ownership request is approved.
     */
    OWNERSHIP_REQUEST_APPROVED,

    /**
     * Notification sent when an ownership request is being analyzed.
     */
    OWNERSHIP_REQUEST_ANALYZING,

    /**
     * Notification sent when an ownership request is rejected.
     */
    OWNERSHIP_REQUEST_REJECTED,

    /**
     * Broadcast notification sent when a new animal is added to the catalog.
     */
    NEW_ANIMAL_ADDED,

    /**
     * Notification sent to fostering users when their fostered animal is adopted.
     */
    FOSTERED_ANIMAL_ADOPTED,

    /**
     * Notification sent when an ownership activity is about to start.
     */
    OWNERSHIP_ACTIVITY_START_REMINDER_USER,

    /**
     * Notification sent when an ownership activity is about to end.
     */
    OWNERSHIP_ACTIVITY_END_REMINDER_USER,

    /**
     * Notification sent when a fostering activity is about to start.
     */
    FOSTERING_ACTIVITY_START_REMINDER_USER,

    /**
     * Notification sent when a fostering activity is about to end.
     */
    FOSTERING_ACTIVITY_END_REMINDER_USER;

    /**
     * Companion object containing utility methods for NotificationType.
     */
    companion object {
        /**
         * Converts a string from the backend to NotificationType enum.
         * Returns null if the type is not recognized or not relevant for users.
         *
         * @param value The notification type string from the backend.
         * @return Corresponding NotificationType or null if not found.
         */
        fun fromString(value: String): NotificationType? {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                null // Unknown or admin-only notification type
            }
        }
    }
}