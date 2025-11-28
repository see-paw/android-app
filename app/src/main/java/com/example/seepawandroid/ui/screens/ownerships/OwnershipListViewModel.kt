package com.example.seepawandroid.ui.screens.ownership

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.managers.OwnershipStateManager
import com.example.seepawandroid.data.remote.dtos.animals.ResOwnedAnimalDto
import com.example.seepawandroid.data.remote.dtos.ownerships.ResOwnershipRequestListDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Ownership List Screen.
 *
 * Observes OwnershipStateManager and transforms state for UI.
 * All data comes from the shared state manager.
 */
@HiltViewModel
class OwnershipListViewModel @Inject constructor(
    private val ownershipStateManager: OwnershipStateManager
) : ViewModel() {

    private val _uiState = MediatorLiveData<OwnershipListUiState>()
    val uiState: LiveData<OwnershipListUiState> = _uiState

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _selectedTabIndex = MutableLiveData(0)
    val selectedTabIndex: LiveData<Int> = _selectedTabIndex

    fun onTabSelected(index: Int) {
        _selectedTabIndex.value = index
    }

    init {
        // Observe ownership requests from state manager
        _uiState.addSource(ownershipStateManager.ownershipRequests) { requests ->
            val currentOwned = ownershipStateManager.ownedAnimals.value ?: emptyList()
            updateUiState(requests, currentOwned, _isRefreshing.value ?: false)
        }

        // Observe owned animals from state manager
        _uiState.addSource(ownershipStateManager.ownedAnimals) { ownedAnimals ->
            val currentRequests = ownershipStateManager.getCurrentRequests()
            updateUiState(currentRequests, ownedAnimals, _isRefreshing.value ?: false)
        }

        // Observe loading state
        _uiState.addSource(ownershipStateManager.isLoading) { isLoading ->
            if (isLoading) {
                // Only show loading if we don't have data yet
                val currentState = _uiState.value
                if (currentState !is OwnershipListUiState.Success) {
                    _uiState.value = OwnershipListUiState.Loading
                }
            }
        }

        // Load data on init (from cache or fetch if empty)
        loadOwnershipRequests()
    }

    /**
     * Updates UI state based on ownership requests.
     *
     * @param requests The list of ownership requests (with images).
     * @param isRefreshing Whether a refresh is in progress.
     */
    private fun updateUiState(
        requests: List<ResOwnershipRequestListDto>,
        ownedAnimals: List<ResOwnedAnimalDto>,
        isRefreshing: Boolean
    ) {
        _uiState.value = when {
            requests.isEmpty() && ownedAnimals.isEmpty() -> OwnershipListUiState.Empty
            else -> OwnershipListUiState.Success(
                activeRequests = requests.sortedByDescending { it.requestedAt },
                ownedAnimals = ownedAnimals.sortedByDescending { it.approvedAt },
                isRefreshing = isRefreshing
            )
        }
    }

    /**
     * Loads ownership requests from state manager (which uses cache or fetches).
     */
    private fun loadOwnershipRequests() {
        viewModelScope.launch {
            val result = ownershipStateManager.fetchAndUpdateState()
            if (result.isFailure) {
                _uiState.value = OwnershipListUiState.Error(
                    result.exceptionOrNull()?.message ?: "Erro ao carregar pedidos"
                )
            }
        }
    }

    /**
     * Refreshes ownership requests (pull-to-refresh).
     */
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true

            val result = ownershipStateManager.fetchAndUpdateState()

            _isRefreshing.value = false

            if (result.isFailure) {
                _uiState.value = OwnershipListUiState.Error(
                    result.exceptionOrNull()?.message ?: "Erro ao atualizar pedidos"
                )
            }
        }
    }

    /**
     * Retries loading after error.
     */
    fun retry() {
        loadOwnershipRequests()
    }
}