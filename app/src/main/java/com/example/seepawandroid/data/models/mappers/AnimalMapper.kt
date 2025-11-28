package com.example.seepawandroid.data.models.mappers

import com.example.seepawandroid.data.local.entities.Animal
import com.example.seepawandroid.data.remote.dtos.animals.ResAnimalDto
import com.example.seepawandroid.data.remote.dtos.breeds.ResBreedDto
import com.example.seepawandroid.data.models.enums.AnimalState
import com.example.seepawandroid.data.models.enums.SexType
import com.example.seepawandroid.data.models.enums.SizeType
import com.example.seepawandroid.data.models.enums.Species
import com.example.seepawandroid.data.remote.dtos.images.ResImageDto

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

/**
 * Maps a local Room entity (Animal) back into a remote API DTO (ResAnimalDto).
 *
 * This reverse mapper:
 * - Reconstructs the breed object using cached data.
 * - Converts image URLs back into ResImageDto objects.
 * - Parses enum values from their string representations.
 *
 * Note: Some metadata (like image IDs and publicIds) cannot be reconstructed
 * from Room and will use placeholder values.
 *
 * @receiver Animal Room entity from local database.
 * @return ResAnimalDto suitable for display in the UI.
 */
fun Animal.toDto(): ResAnimalDto {
    // Reconstruct breed object
    val breedDto = ResBreedDto(
        id = breedId ?: "",
        name = breedName ?: "Unknown",
        description = null
    )

    // Reconstruct images list
    val imagesList = if (imageUrls.isNotEmpty()) {
        imageUrls.mapIndexed { index, url ->
            ResImageDto(
                id = "", // Not stored in Room
                publicId = "", // Not stored in Room
                isPrincipal = (url == imageUrl), // Principal if it matches main image
                url = url,
                description = "" // Not stored in Room
            )
        }
    } else {
        // If no images, create one from imageUrl if available
        imageUrl?.let {
            listOf(
                ResImageDto(
                    id = "",
                    publicId = "",
                    isPrincipal = true,
                    url = it,
                    description = ""
                )
            )
        }
    }

    return ResAnimalDto(
        id = id,
        name = name,
        species = Species.valueOf(species),
        size = SizeType.valueOf(size),
        sex = SexType.valueOf(sex),
        breed = breedDto,
        animalState = AnimalState.valueOf(animalState),
        colour = colour,
        birthDate = birthDate,
        age = age,
        description = description,
        sterilized = sterilized,
        features = features,
        cost = cost,
        shelterId = shelterId,
        images = imagesList
    )
}
