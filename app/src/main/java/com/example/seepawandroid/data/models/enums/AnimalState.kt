package com.example.seepawandroid.data.models.enums

/**
 * Represents the current adoption or availability state of an animal.
 */
enum class AnimalState {
    /**
     * The animal is available for adoption or fostering.
     */
    Available,

    /**
     * The animal is currently partially fostered.
     */
    PartiallyFostered,

    /**
     * The animal is fully fostered and not available.
     */
    TotallyFostered,

    /**
     * The animal already has an assigned owner.
     */
    HasOwner,

    /**
     * The animal is inactive, archived, or not available.
     */
    Inactive
}
