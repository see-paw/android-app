package com.example.seepawandroid.ui.screens.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.repositories.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
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
                    } else {
                        FavoritesUiState.Success(
                            favorites = favoritesDto.items,
                            pageSize = favoritesDto.pageSize,
                            totalCount = favoritesDto.totalCount,
                            currentPage = favoritesDto.currentPage
                        )
                    }
                },
                onFailure = { error ->
                    FavoritesUiState.Error(
                        message = error.message ?: "Error loading favorites"
                    )
                }
            )
        }
    }

    /**
     * Removes an animal from the user's favorites.
     * After successful removal, reloads the favorites list.
     *
     * @param animalId The ID of the animal to remove from favorites
     */
    fun removeFavorite(animalId: String) {
        viewModelScope.launch {
            try {
                val result = favoriteRepository.removeFavorite(animalId)

                result.onSuccess {
                    // Reload favorites after successful removal
                    loadFavorites()
                    android.util.Log.d("FavoritesViewModel", "Favorite removed successfully: $animalId")
                }.onFailure { error ->
                    android.util.Log.e("FavoritesViewModel", "Error removing favorite", error)
                    // Optionally show error to user
                }
            } catch (e: Exception) {
                android.util.Log.e("FavoritesViewModel", "Error removing favorite", e)
            }
        }
    }
}