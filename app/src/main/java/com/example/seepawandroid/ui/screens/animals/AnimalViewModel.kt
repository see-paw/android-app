package com.example.seepawandroid.ui.screens.animals.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.local.entities.Animal
import com.example.seepawandroid.data.remote.dtos.Animals.AnimalFilterDto
import com.example.seepawandroid.data.repositories.AnimalRepository
import com.example.seepawandroid.ui.screens.Animals.AnimalCatalogueUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalViewModel @Inject constructor(
    private val repository: AnimalRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<AnimalCatalogueUiState>()
    val uiState: LiveData<AnimalCatalogueUiState> = _uiState

    private var lastFullList: List<Animal> = emptyList()

    fun loadAnimals(
        filters: AnimalFilterDto? = null,
        sortBy: String? = null,
        order: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = AnimalCatalogueUiState.Loading

            val result = repository.getAnimals(filters, sortBy, order)

            if (result.isEmpty()) {
                _uiState.value = AnimalCatalogueUiState.Empty
            } else {
                lastFullList = result
                _uiState.value = AnimalCatalogueUiState.Success(result)
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            // restore full list
            if (lastFullList.isNotEmpty()) {
                _uiState.value = AnimalCatalogueUiState.Success(lastFullList)
            } else {
                _uiState.value = AnimalCatalogueUiState.Empty
            }
            return
        }

        val filtered = lastFullList.filter {
            it.name.contains(query, ignoreCase = true)
        }

        if (filtered.isEmpty()) {
            _uiState.value = AnimalCatalogueUiState.Empty
        } else {
            _uiState.value = AnimalCatalogueUiState.Success(filtered)
        }
    }
}
