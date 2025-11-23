package com.example.seepawandroid.data.remote.dtos.animals

import com.example.seepawandroid.data.models.enums.AnimalState
import com.example.seepawandroid.data.models.enums.SexType
import com.example.seepawandroid.data.models.enums.SizeType
import com.example.seepawandroid.data.models.enums.Species
import com.example.seepawandroid.data.remote.dtos.breeds.ResBreedDto
import com.example.seepawandroid.data.remote.dtos.images.ResImageDto

/**
 * DTO representing a single animal returned from the backend API.
 *
 * This class reflects the JSON structure received from the server.
 * It includes information about the animal’s identity, classification,
 * biological attributes, status, physical characteristics, and associated images.
 *
 * @property id Unique identifier of the animal.
 * @property name The animal’s name.
 * @property species Species classification (Dog or Cat).
 * @property size The animal’s size category.
 * @property sex Biological sex (Male or Female).
 * @property breed Breed information (id, name, description).
 * @property animalState Current adoption/fostering state of the animal.
 * @property colour The animal’s colour.
 * @property birthDate Date of birth in yyyy-MM-dd format.
 * @property age Age in years.
 * @property description Optional additional description.
 * @property sterilized Whether the animal has been sterilized.
 * @property features Optional additional traits.
 * @property cost Adoption cost.
 * @property shelterId ID of the shelter that owns the animal.
 * @property images Optional list of associated image objects.
 */
data class ResAnimalDto(
    val id: String,
    val name: String,
    val species: Species,
    val size: SizeType,
    val sex: SexType,
    val breed: ResBreedDto,
    val animalState: AnimalState,
    val colour: String,
    val birthDate: String,   // Backend envia DateOnly como "yyyy-MM-dd"
    val age: Int,
    val description: String?,
    val sterilized: Boolean,
    val features: String?,
    val cost: Double,
    val shelterId: String,
    val images: List<ResImageDto>?
)
