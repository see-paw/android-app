package com.example.seepawandroid.ui.screens.fosterings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.remote.dtos.fosterings.ResActiveFosteringIdDto
import com.example.seepawandroid.data.repositories.AnimalRepository
import com.example.seepawandroid.data.repositories.FosteringRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Fostering List Screen.
 *
 * Manages loading and refreshing of active fosterings.
 */
@HiltViewModel
class FosteringListViewModel @Inject constructor(
    private val fosteringRepository: FosteringRepository,
    private val animalRepository: AnimalRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<FosteringListUiState>(FosteringListUiState.Loading)
    /**
     * The current state of the UI.
     */
    val uiState: LiveData<FosteringListUiState> = _uiState

    private val _selectedTabIndex = MutableLiveData(0)
    /**
     * The currently selected tab index.
     */
    val selectedTabIndex: LiveData<Int> = _selectedTabIndex

    private val _showCancelDialog = MutableLiveData<String?>(null)
    /**
     * The ID of the fostering to cancel, or null if dialog is hidden.
     */
    val showCancelDialog: LiveData<String?> = _showCancelDialog

    private val _fosteringIds = MutableLiveData<List<ResActiveFosteringIdDto>>(emptyList())

    private val _animalNameToIdMap = MutableLiveData<Map<String, String>>(emptyMap())

    init {
        loadFosterings()
    }

    /**
     * Called when a tab is selected.
     *
     * @param index The index of the selected tab.
     */
    fun onTabSelected(index: Int) {
        _selectedTabIndex.value = index
    }

    /**
     * Shows the cancel confirmation dialog.
     *
     * @param animalName The name of the animal whose fostering will be cancelled.
     */
    fun showCancelDialog(animalName: String) {
        _showCancelDialog.value = animalName
    }

    /**
     * Dismisses the cancel confirmation dialog.
     */
    fun dismissCancelDialog() {
        _showCancelDialog.value = null
    }

    /**
     * Loads active fosterings from the repository.
     */
    private fun loadFosterings() {
        viewModelScope.launch {
            _uiState.value = FosteringListUiState.Loading

            try {
                val dataResult = fosteringRepository.getActiveFosterings()
                val idsResult = fosteringRepository.getActiveFosteringIds()

                if (dataResult.isSuccess && idsResult.isSuccess) {
                    val fosterings = dataResult.getOrNull() ?: emptyList()
                    val ids = idsResult.getOrNull() ?: emptyList()

                    _fosteringIds.value = ids

                    val nameToIdMap = mutableMapOf<String, String>()
                    for (idDto in ids) {
                        val animalResult = animalRepository.getAnimalById(idDto.animalId)
                        if (animalResult.isSuccess) {
                            val (animal, _) = animalResult.getOrNull() ?: continue
                            nameToIdMap[animal.name] = idDto.animalId
                        }
                    }
                    _animalNameToIdMap.value = nameToIdMap

                    _uiState.value = if (fosterings.isEmpty()) {
                        FosteringListUiState.Empty
                    } else {
                        FosteringListUiState.Success(
                            fosterings = fosterings.sortedByDescending { it.startDate }
                        )
                    }
                } else {
                    val error = dataResult.exceptionOrNull() ?: idsResult.exceptionOrNull()
                    _uiState.value = FosteringListUiState.Error(
                        error?.message ?: "Erro ao carregar apadrinhamentos"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = FosteringListUiState.Error(
                    e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    /**
     * Refreshes the fostering list (pull-to-refresh).
     */
    fun refresh() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is FosteringListUiState.Success) {
                _uiState.value = currentState.copy(isRefreshing = true)
            }

            try {
                val dataResult = fosteringRepository.getActiveFosterings()
                val idsResult = fosteringRepository.getActiveFosteringIds()

                if (dataResult.isSuccess && idsResult.isSuccess) {
                    val fosterings = dataResult.getOrNull() ?: emptyList()
                    val ids = idsResult.getOrNull() ?: emptyList()

                    _fosteringIds.value = ids

                    val nameToIdMap = mutableMapOf<String, String>()
                    for (idDto in ids) {
                        val animalResult = animalRepository.getAnimalById(idDto.animalId)
                        if (animalResult.isSuccess) {
                            val (animal, _) = animalResult.getOrNull() ?: continue
                            nameToIdMap[animal.name] = idDto.animalId
                        }
                    }
                    _animalNameToIdMap.value = nameToIdMap

                    _uiState.value = if (fosterings.isEmpty()) {
                        FosteringListUiState.Empty
                    } else {
                        FosteringListUiState.Success(
                            fosterings = fosterings.sortedByDescending { it.startDate },
                            isRefreshing = false
                        )
                    }
                } else {
                    _uiState.value = FosteringListUiState.Error(
                        dataResult.exceptionOrNull()?.message ?: "Erro ao atualizar"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = FosteringListUiState.Error(
                    e.message ?: "Erro ao atualizar"
                )
            }
        }
    }

    /**
     * Cancels a fostering.
     *
     * @param fosteringId The ID of the fostering to cancel.
     */
    fun cancelFostering() {
        val animalName = _showCancelDialog.value ?: return

        viewModelScope.launch {
            dismissCancelDialog()

            val ids = _fosteringIds.value ?: emptyList()
            if (ids.isEmpty()) {
                _uiState.value = FosteringListUiState.Error("Erro ao encontrar apadrinhamento")
                return@launch
            }

            var fosteringId: String? = null

            // Iterar pelos IDs e fazer fetch de cada animal até encontrar o match
            for (idDto in ids) {
                val animalResult = animalRepository.getAnimalById(idDto.animalId)

                if (animalResult.isSuccess) {
                    val (animal, _) = animalResult.getOrNull() ?: continue

                    if (animal.name == animalName) {
                        // Encontrei! Este é o fostering ID correto
                        fosteringId = idDto.id
                        break
                    }
                }
            }

            if (fosteringId == null) {
                _uiState.value = FosteringListUiState.Error("Não foi possível encontrar o apadrinhamento")
                return@launch
            }

            try {
                val result = fosteringRepository.cancelFostering(fosteringId)

                if (result.isSuccess) {
                    loadFosterings()
                } else {
                    _uiState.value = FosteringListUiState.Error(
                        result.exceptionOrNull()?.message ?: "Erro ao cancelar apadrinhamento"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = FosteringListUiState.Error(
                    e.message ?: "Erro ao cancelar"
                )
            }
        }
    }

    /**
     * Gets the animal ID for a given animal name.
     *
     * @param animalName The name of the animal.
     * @return The animal ID, or null if not found.
     */
    fun getAnimalId(animalName: String): String? {
        return _animalNameToIdMap.value?.get(animalName)
    }

    /**
     * Retries loading after error.
     */
    fun retry() {
        loadFosterings()
    }
}