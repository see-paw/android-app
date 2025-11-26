package com.example.seepawandroid.ui.screens.animals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.managers.OwnershipStateManager
import com.example.seepawandroid.data.models.enums.AnimalState
import com.example.seepawandroid.data.repositories.AnimalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Animal Detail screen.
 *
 * Manages:
 * - Loading animal details from backend or Room cache
 * - Determining online/offline state
 * - Verifying if user already has an ownership request for this animal
 * - Exposing UI state for the screen
 *
 * The ViewModel distinguishes between:
 * - Success (online): Full functionality, action buttons enabled
 * - SuccessOffline: Display only, action buttons disabled
 * - Error: Failed to load with appropriate message
 */
@HiltViewModel
class AnimalDetailViewModel @Inject constructor(
    private val repository: AnimalRepository,
    private val ownershipStateManager: OwnershipStateManager
) : ViewModel() {

    private val _uiState = MutableLiveData<AnimalDetailUiState>(AnimalDetailUiState.Loading)
    val uiState: LiveData<AnimalDetailUiState> = _uiState

    private val _showLoginDialog = MutableLiveData(false)
    val showLoginDialog: LiveData<Boolean> = _showLoginDialog

    private val _showOwnershipExistsDialog = MutableLiveData<String?>()
    val showOwnershipExistsDialog: LiveData<String?> = _showOwnershipExistsDialog

    /**
     * Shows the login required dialog.
     */
    fun showLoginDialog() {
        _showLoginDialog.value = true
    }

    /**
     * Dismisses the login required dialog.
     */
    fun dismissLoginDialog() {
        _showLoginDialog.value = false
    }

    /**
     * Shows the ownership exists dialog with animal name.
     */
    private fun showOwnershipExistsDialog(animalName: String) {
        _showOwnershipExistsDialog.value = animalName
    }

    /**
     * Dismisses the ownership exists dialog.
     */
    fun dismissOwnershipExistsDialog() {
        _showOwnershipExistsDialog.value = null
    }

    /**
     * Handles ownership button click with verification.
     * Checks if user already has an ownership request for this animal.
     *
     * @param animalId The ID of the animal.
     * @param animalName The name of the animal.
     * @return True if should navigate to ownership wizard, false if should show dialog.
     */
    fun onOwnershipButtonClick(animalId: String, animalName: String): Boolean {
        return if (ownershipStateManager.hasOwnershipRequestForAnimal(animalId)) {
            // User already has a request for this animal
            showOwnershipExistsDialog(animalName)
            false // Don't navigate
        } else {
            // No existing request, allow navigation
            true
        }
    }

    /**
     * Loads animal details by ID.
     *
     * Attempts to fetch from backend first. If offline or API fails,
     * falls back to Room cache.
     *
     * @param animalId The unique identifier of the animal to load.
     */
    fun loadAnimal(animalId: String) {
        viewModelScope.launch {
            _uiState.value = AnimalDetailUiState.Loading

            try {
                val result = repository.getAnimalById(animalId)

                if (result.isSuccess) {
                    val (animal, isOffline) = result.getOrNull()!!

                    _uiState.value = if (isOffline) {
                        // Offline: no actions available
                        AnimalDetailUiState.SuccessOffline(
                            animal = animal,
                            canFoster = false,
                            canRequestOwnership = false
                        )
                    } else {
                        // Online: check animal state
                        val canPerformActions = animal.animalState != AnimalState.HasOwner
                                && animal.animalState != AnimalState.Inactive

                        AnimalDetailUiState.Success(
                            animal = animal,
                            canFoster = canPerformActions,
                            canRequestOwnership = canPerformActions
                        )
                    }
                } else {
                    val exception = result.exceptionOrNull()
                    val needsInternet = exception?.message?.contains("No internet") == true

                    _uiState.value = AnimalDetailUiState.Error(
                        message = exception?.message ?: "Failed to load animal details",
                        needsInternet = needsInternet
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AnimalDetailUiState.Error(
                    message = e.message ?: "Unknown error occurred",
                    needsInternet = false
                )
            }
        }
    }

    /**
     * Resets the UI state back to Loading.
     * Useful when retrying after an error.
     */
    fun retry(animalId: String) {
        loadAnimal(animalId)
    }
}