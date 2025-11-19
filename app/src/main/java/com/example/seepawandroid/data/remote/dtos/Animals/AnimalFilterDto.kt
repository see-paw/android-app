package com.example.seepawandroid.data.remote.dtos.Animals

data class AnimalFilterDto(
    val species: String? = null,
    val age: Int? = null,
    val size: String? = null,
    val color: String? = null,
    val sex: String? = null,
    val name: String? = null,
    val shelterName: String? = null,
    val breed: String? = null
)
