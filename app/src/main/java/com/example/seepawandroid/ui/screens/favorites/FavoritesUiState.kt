package com.example.seepawandroid.ui.screens.favorites

import com.example.seepawandroid.data.remote.dtos.favorites.ResGetFavoritesDto

sealed class FavoritesUiState {
    object Loading: FavoritesUiState()

    data class Success(
        val favorites: List<ResGetFavoritesDto>,
        val pageSize: Int,
        val totalCount: Int,
        val currentPage: Int
    ): FavoritesUiState()

    data class Error(
        val message: String
    ): FavoritesUiState()

    object Empty: FavoritesUiState()
}
