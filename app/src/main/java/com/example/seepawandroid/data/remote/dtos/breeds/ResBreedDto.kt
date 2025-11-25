package com.example.seepawandroid.data.remote.dtos.breeds

/**
 * DTO representing the breed information of an animal.
 *
 * @property id Unique identifier of the breed.
 * @property name The name of the breed.
 * @property description Optional description of the breed.
 */
data class ResBreedDto(
    val id: String,
    val name: String,
    val description: String?
)
