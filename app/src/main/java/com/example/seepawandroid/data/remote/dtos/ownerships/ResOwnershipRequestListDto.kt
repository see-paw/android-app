package com.example.seepawandroid.data.remote.dtos.ownerships

import com.example.seepawandroid.data.models.enums.AnimalState
import com.example.seepawandroid.data.models.enums.OwnershipStatus
import com.example.seepawandroid.data.remote.dtos.images.ResImageDto
import com.google.gson.annotations.SerializedName

/**
 * Response DTO for ownership request in list view.
 *
 * Received from GET /api/OwnershipRequests/user-requests
 * Includes animal image and state information.
 *
 * @property id Unique identifier of the ownership request.
 * @property animalId ID of the animal being requested.
 * @property animalName Name of the animal.
 * @property animalState Current state of the animal (Available, Adopted, etc).
 * @property image Principal image of the animal.
 * @property amount Mock payment amount.
 * @property status Current status of the request (mapped from "ownershipStatus" field).
 * @property requestInfo Additional information provided by the user.
 * @property requestedAt Timestamp when the request was created.
 * @property approvedAt Timestamp when the request was approved (if applicable).
 * @property updatedAt Timestamp of the last update.
 */
data class ResOwnershipRequestListDto(
    val id: String,
    val animalId: String,
    val animalName: String,
    val animalState: AnimalState,
    val image: ResImageDto,
    val amount: Double,
    @SerializedName("ownershipStatus")
    val status: OwnershipStatus,
    val requestInfo: String?,
    val requestedAt: String,
    val approvedAt: String?,
    val updatedAt: String?
)