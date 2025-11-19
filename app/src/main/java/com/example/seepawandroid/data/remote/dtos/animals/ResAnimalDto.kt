package com.example.seepawandroid.data.remote.dtos.animals

import com.example.seepawandroid.data.models.enums.AnimalState
import com.example.seepawandroid.data.models.enums.SexType
import com.example.seepawandroid.data.models.enums.SizeType
import com.example.seepawandroid.data.models.enums.Species
import com.example.seepawandroid.data.remote.dtos.breeds.ResBreedDto
import com.example.seepawandroid.data.remote.dtos.images.ResImageDto


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