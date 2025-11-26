package com.example.seepawandroid.data.remote.dtos.ownerships

/**
 * Request DTO for creating an ownership request.
 *
 * Sent to POST /api/OwnershipRequests
 *
 * @property animalId The unique identifier of the animal to adopt.
 */
data class ReqOwnershipRequestDto(
    val animalId: String
)