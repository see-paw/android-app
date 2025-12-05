package com.example.seepawandroid.data.remote.dtos.ownerships

import com.example.seepawandroid.data.models.enums.OwnershipStatus

/**
 * Response DTO for creating an ownership request.
 *
 * Received from POST /api/OwnershipRequests
 * Uses "status" field (without ownershipStatus prefix).
 *
 * @property id Unique identifier of the ownership request.
 * @property animalId ID of the animal being requested.
 * @property animalName Name of the animal.
 * @property userId ID of the user making the request.
 * @property userName Name of the user.
 * @property amount Mock payment amount.
 * @property status Current status of the request (mapped from "status" field).
 * @property requestInfo Additional information provided by the user.
 * @property requestedAt Timestamp when the request was created.
 * @property approvedAt Timestamp when the request was approved (if applicable).
 * @property updatedAt Timestamp of the last update.
 */
data class ResOwnershipRequestDto(
    val id: String,
    val animalId: String,
    val animalName: String,
    val userId: String,
    val userName: String,
    val amount: Double,
    val status: OwnershipStatus,
    val requestInfo: String?,
    val requestedAt: String,
    val approvedAt: String?,
    val updatedAt: String?
)