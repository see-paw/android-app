package com.example.seepawandroid.data.models

import com.example.seepawandroid.data.local.entities.Animal

/**
 * Data class representing the structure of a paginated response for animals.
 *
 * This model is typically returned from the backend API and contains
 * both the list of animals and pagination metadata.
 *
 * @property items The list of Animal objects for the current page.
 * @property currentPage The index of the current page.
 * @property totalPages The total number of pages available.
 * @property totalCount The total number of animals available.
 */
data class PagedAnimals(
    val items: List<Animal>,
    val currentPage: Int,
    val totalPages: Int,
    val totalCount: Int
)
