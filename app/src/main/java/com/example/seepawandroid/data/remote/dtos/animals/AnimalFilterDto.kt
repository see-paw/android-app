package com.example.seepawandroid.data.remote.dtos.Animals

/**
 * Data Transfer Object used to represent filter options when querying animals.
 *
 * This DTO is typically used when sending filter parameters to the backend API.
 * All fields are optional. Only non-null parameters will be applied as filters.
 *
 * @property species Optional species filter (e.g., "Dog", "Cat").
 * @property age Optional age filter in years.
 * @property size Optional size filter (Small, Medium, Large).
 * @property sex Optional sex filter (Male or Female).
 * @property name Optional text search for animal name.
 * @property shelterName Optional filter by the shelter's name.
 * @property breed Optional breed filter.
 */
data class AnimalFilterDto(
    val species: String? = null,
    val age: Int? = null,
    val size: String? = null,
    val sex: String? = null,
    val name: String? = null,
    val shelterName: String? = null,
    val breed: String? = null
)
