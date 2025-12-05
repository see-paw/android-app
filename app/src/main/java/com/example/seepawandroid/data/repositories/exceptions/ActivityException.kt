package com.example.seepawandroid.data.repositories.exceptions

/**
 * Base sealed class for activity-related exceptions.
 * This sealed class encapsulates different types of errors, providing specific details for each case.
 *
 * @param message A descriptive message for the exception.
 */
sealed class ActivityException(message: String) : Exception(message) {
    /**
     * Exception thrown when a time slot is already booked by another user.
     *
     * @property slotId The ID of the slot that is already booked.
     */
    data class SlotAlreadyBookedException(
        val slotId: String? = null
    ) : ActivityException("This time slot has already been booked by another user")

    /**
     * Exception thrown when slot data is invalid.
     *
     * @property reason The reason why the slot is invalid.
     */
    data class InvalidSlotException(
        val reason: String
    ) : ActivityException(reason)

    /**
     * Exception thrown when a server error occurs.
     *
     * @property code The HTTP status code.
     */
    data class ServerException(
        val code: Int
    ) : ActivityException("Server error (HTTP $code). Please try again later")

    /**
     * Exception thrown when a network error occurs.
     *
     * @property cause The underlying cause of the network error.
     */
    data class NetworkException(
        override val cause: Throwable
    ) : ActivityException("Network error. Please check your connection")

    /**
     * Exception thrown for unknown errors.
     *
     * @property code The HTTP status code.
     * @property errorBody The error response body.
     */
    data class UnknownException(
        val code: Int,
        val errorBody: String?
    ) : ActivityException("Unknown error (HTTP $code)")
}
