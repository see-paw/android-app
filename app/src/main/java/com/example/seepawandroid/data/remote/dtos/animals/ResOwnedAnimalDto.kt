package com.example.seepawandroid.data.remote.dtos.animals

import com.example.seepawandroid.data.models.enums.AnimalState
import com.example.seepawandroid.data.models.enums.OwnershipStatus
import com.example.seepawandroid.data.remote.dtos.images.ResImageDto

/**
 * Response DTO for owned animals (approved ownership requests).
 *
 * Received from GET /api/OwnershipRequests/owned-animals
 * Represents animals that the user already owns (approved requests).
 *
 * @property id Unique identifier (same as animalId).
 * @property animalId ID of the animal.
 * @property animalName Name of the animal.
 * @property animalState Current state of the animal (should be "HasOwner").
 * @property image Principal image of the animal (can be null).
 * @property amount Adoption cost paid.
 * @property ownershipStatus Always null for approved requests.
 * @property requestInfo Additional info from the request.
 * @property requestedAt When the ownership was requested.
 * @property approvedAt When the ownership was approved.
 * @property updatedAt Last update timestamp.
 */
data class ResOwnedAnimalDto(
    val id: String,
    val animalId: String,
    val animalName: String,
    val animalState: AnimalState,
    val image: ResImageDto?,
    val amount: Double,
    val ownershipStatus: OwnershipStatus?,  // Always null when approved
    val requestInfo: String?,
    val requestedAt: String,
    val approvedAt: String,
    val updatedAt: String?
)