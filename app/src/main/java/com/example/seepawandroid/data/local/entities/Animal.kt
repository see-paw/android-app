package com.example.seepawandroid.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing an Animal stored in the Room local database.
 *
 * This model mirrors the structure returned from the backend API and is used
 * for offline caching. Room will generate a table named "animals" based on this entity.
 *
 * @property id Unique identifier of the animal.
 * @property name The name of the animal.
 * @property species The species (e.g., dog, cat).
 * @property size The size category (small, medium, large).
 * @property sex The biological sex of the animal.
 * @property breedName Optional display name of the breed.
 * @property breedId Optional backend breed identifier.
 * @property animalState Current adoption status (e.g., available, adopted).
 * @property colour The animal's colour.
 * @property birthDate The date of birth in string format.
 * @property age The computed age of the animal.
 * @property description Optional textual description.
 * @property sterilized Whether the animal has been sterilized.
 * @property features Optional key features or notes.
 * @property cost Adoption cost.
 * @property shelterId Identifier of the shelter where the animal is located.
 * @property imageUrl Primary image URL.
 * @property imageUrls Additional image URLs stored as a List<String>.
 */
@Entity(tableName = "animals")
data class Animal(
    @PrimaryKey val id: String,
    val name: String,
    val species: String,
    val size: String,
    val sex: String,
    val breedName: String?,
    val breedId: String?,
    val animalState: String,
    val colour: String,
    val birthDate: String,
    val age: Int,
    val description: String?,
    val sterilized: Boolean,
    val features: String?,
    val cost: Double,
    val shelterId: String,
    val imageUrl: String?,
    val imageUrls: List<String>,
    val isFavorite: Boolean = false
)
