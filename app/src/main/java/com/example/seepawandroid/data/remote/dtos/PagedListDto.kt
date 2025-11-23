package com.example.seepawandroid.data.remote.dtos

/**
 * Generic DTO representing a paginated API response.
 *
 * @param T The type of items contained in the paginated list.
 *
 * @property items List of items for the current page.
 * @property totalCount Total number of items available across all pages.
 * @property currentPage Index of the current page (1-based).
 * @property pageSize Number of items returned per page.
 */
data class PagedListDto<T>(
    val items: List<T>,
    val totalCount: Int,
    val currentPage: Int,
    val pageSize: Int
)
