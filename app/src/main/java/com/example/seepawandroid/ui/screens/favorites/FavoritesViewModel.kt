package com.example.seepawandroid.ui.screens.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.repositories.FavoriteRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<FavoritesUiState>()
    val uiState: LiveData<FavoritesUiState> = _uiState

    fun loadFavorites(
        pageNumber: Int = 1,
        pageSize: Int = 10
    ){
        viewModelScope.launch {
            _uiState.value = FavoritesUiState.Loading

            val result = favoriteRepository.getFavorites(pageNumber, pageSize)

            _uiState.value = result.fold(
                onSuccess = { favoritesDto ->
                    if (favoritesDto.items.isEmpty()) {
                        FavoritesUiState.Empty
                    }

                    FavoritesUiState.Success(
                        favorites = favoritesDto.items,
                        pageSize = favoritesDto.pageSize,
                        totalCount = favoritesDto.totalCount,
                        currentPage = favoritesDto.currentPage
                    )
                },
                onFailure = {

                }
            )
        }
    }
}