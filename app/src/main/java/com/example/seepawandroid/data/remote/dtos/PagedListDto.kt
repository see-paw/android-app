package com.example.seepawandroid.data.remote.dtos

data class PagedListDto<T>(
    val items: List<T>,
    val totalCount: Int,
    val currentPage: Int,
    val pageSize: Int
)
