package com.example.seepawandroid.data.models.enums

/**
 * Represents the status of an ownership request.
 *
 * Mirrors the backend OwnershipStatus enum to track
 * the lifecycle of an adoption request.
 */
enum class OwnershipStatus {
    /**
     * The ownership request was initialized and is awaiting review.
     */
    Pending,

    /**
     * The ownership request is currently being analyzed by shelter staff.
     */
    Analysing,

    /**
     * The ownership request was approved. User can now adopt the animal.
     */
    Approved,

    /**
     * The ownership request was rejected.
     */
    Rejected
}