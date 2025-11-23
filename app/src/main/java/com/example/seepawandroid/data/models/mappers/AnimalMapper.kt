package com.example.seepawandroid.data.models.mappers

import com.example.seepawandroid.data.local.entities.Animal
import com.example.seepawandroid.data.remote.dtos.animals.ResAnimalDto

/**
 * Maps a remote API DTO (ResAnimalDto) into a local Room entity (Animal).
 *
 * This mapper:
 * - Extracts the list of images from the DTO.
 * - Selects the main image (image marked as principal, otherwise first available).
 * - Converts enums to their string name representation for local storage.
 * - Ensures all images are converted into a String list for Room.
 *
 * @receiver ResAnimalDto received from the backend.
 * @return Animal Room entity ready to be stored locally.
 */
fun ResAnimalDto.toEntity(): Animal {
    val imageList = this.images ?: emptyList()

    // Select the main image: principal image if available, else first image
    val mainImage = imageList.firstOrNull { it.isPrincipal }?.url
        ?: imageList.firstOrNull()?.url

    // Collect all URLs (ignoring null entries)
    val allImages = imageList.mapNotNull { it.url }

    return Animal(
        id = id,
        name = name,
        species = species.name,
        size = size.name,
        sex = sex.name,
        breedName = breed.name,
        breedId = breed.id,
        animalState = animalState.name,
        colour = colour,
        birthDate = birthDate,
        age = age,
        description = description,
        sterilized = sterilized,
        features = features,
        cost = cost.toDouble(),
        shelterId = shelterId,
        imageUrl = mainImage,
        imageUrls = allImages
    )
}
