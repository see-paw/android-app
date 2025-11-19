package com.example.seepawandroid.data.models.mappers

import com.example.seepawandroid.data.local.entities.Animal
import com.example.seepawandroid.data.remote.dtos.Animals.ResAnimalDto

fun ResAnimalDto.toEntity(): Animal {
    val mainImage = this.images?.firstOrNull { it.isPrincipal }?.url
        ?: this.images?.firstOrNull()?.url

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
        imageUrl = mainImage
    )
}
