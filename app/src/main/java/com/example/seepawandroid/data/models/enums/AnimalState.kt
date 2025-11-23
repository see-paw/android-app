package com.example.seepawandroid.data.models.enums

/**
 * Represents the current adoption or availability state of an animal.
 *
 * Possible values:
 * - Available: The animal is available for adoption or fostering.
 * - PartiallyFostered: The animal is currently partially fostered.
 * - TotallyFostered: The animal is fully fostered and not available.
 * - HasOwner: The animal already has an assigned owner.
 * - Inactive: The animal is inactive, archived, or not available.
 */
enum class AnimalState {
    Available,
    PartiallyFostered,
    TotallyFostered,
    HasOwner,
    Inactive
}
