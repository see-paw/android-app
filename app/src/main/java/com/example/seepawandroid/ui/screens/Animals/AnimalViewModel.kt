package com.example.seepawandroid.ui.screens.animals.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.local.entities.Animal
import com.example.seepawandroid.data.remote.dtos.Animals.AnimalFilterDto
import com.example.seepawandroid.data.repositories.AnimalRepository
import com.example.seepawandroid.ui.screens.Animals.AnimalListUiState
import kotlinx.coroutines.launch

class AnimalViewModel(
    private val repository: AnimalRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<AnimalListUiState>()
    val uiState: LiveData<AnimalListUiState> = _uiState

    private var lastFullList: List<Animal> = emptyList()

    fun loadAnimals(
        filters: AnimalFilterDto? = null,
        sortBy: String? = null,
        order: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = AnimalListUiState.Loading

            val result = repository.getAnimals(filters, sortBy, order)

            if (result.isEmpty()) {
                _uiState.value = AnimalListUiState.Empty
            } else {
                lastFullList = result
                _uiState.value = AnimalListUiState.Success(result)
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            // restore full list
            if (lastFullList.isNotEmpty()) {
                _uiState.value = AnimalListUiState.Success(lastFullList)
            } else {
                _uiState.value = AnimalListUiState.Empty
            }
            return
        }

        val filtered = lastFullList.filter {
            it.name.contains(query, ignoreCase = true)
        }

        if (filtered.isEmpty()) {
            _uiState.value = AnimalListUiState.Empty
        } else {
            _uiState.value = AnimalListUiState.Success(filtered)
        }
    }
}
