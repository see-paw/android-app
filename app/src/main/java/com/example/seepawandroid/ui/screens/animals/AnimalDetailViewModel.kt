package com.example.seepawandroid.ui.screens.animals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.managers.OwnershipStateManager
import com.example.seepawandroid.data.models.enums.AnimalState
import com.example.seepawandroid.data.repositories.AnimalRepository
import com.example.seepawandroid.data.repositories.FosteringRepository
import com.example.seepawandroid.ui.models.MockPaymentData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

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
    private val ownershipStateManager: OwnershipStateManager,
    private val fosteringRepository: FosteringRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<AnimalDetailUiState>(AnimalDetailUiState.Loading)
    /**
     * The current state of the UI.
     */
    val uiState: LiveData<AnimalDetailUiState> = _uiState

    private val _showLoginDialog = MutableLiveData(false)
    /**
     * Whether to show the login dialog.
     */
    val showLoginDialog: LiveData<Boolean> = _showLoginDialog

    private val _showOwnershipExistsDialog = MutableLiveData<String?>()
    /**
     * The name of the animal for which an ownership request already exists, or null.
     */
    val showOwnershipExistsDialog: LiveData<String?> = _showOwnershipExistsDialog

    // Fostering dialog state
    private val _showFosteringDialog = MutableLiveData(false)
    val showFosteringDialog: LiveData<Boolean> = _showFosteringDialog

    private val _selectedFosteringAmount = MutableLiveData<Double?>(null)
    val selectedFosteringAmount: LiveData<Double?> = _selectedFosteringAmount

    private val _customFosteringAmount = MutableLiveData("")
    val customFosteringAmount: LiveData<String> = _customFosteringAmount

    private val _fosteringAmountError = MutableLiveData(false)
    val fosteringAmountError: LiveData<Boolean> = _fosteringAmountError

    private val _fosteringResult = MutableLiveData<Result<Unit>?>(null)
    val fosteringResult: LiveData<Result<Unit>?> = _fosteringResult

    // Mock payment dialog state
    private val _showPaymentMockDialog = MutableLiveData(false)
    /**
     * Whether to show the payment mock dialog.
     */
    val showPaymentMockDialog: LiveData<Boolean> = _showPaymentMockDialog

    private val _mockPaymentData = MutableLiveData<MockPaymentData>()
    /**
     * Mock payment data to display.
     */
    val mockPaymentData: LiveData<MockPaymentData> = _mockPaymentData

    private var pendingFosteringAnimalId: String? = null
    var pendingFosteringAmount: Double? = null
        private set

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
     * Dismisses the payment mock dialog.
     */
    fun dismissPaymentMockDialog() {
        _showPaymentMockDialog.value = false
    }

    /**
     * Shows the fostering amount selection dialog.
     */
    fun showFosteringDialog() {
        _showFosteringDialog.value = true
        // Reset state when opening dialog
        _selectedFosteringAmount.value = null
        _customFosteringAmount.value = ""
        _fosteringAmountError.value = false
    }

    /**
     * Dismisses the fostering amount selection dialog.
     */
    fun dismissFosteringDialog() {
        _showFosteringDialog.value = false
    }

    /**
     * Updates the selected fostering amount.
     */
    fun selectFosteringAmount(amount: Double?) {
        _selectedFosteringAmount.value = amount
        if (amount != -1.0) {
            _customFosteringAmount.value = ""
        }
        _fosteringAmountError.value = false
    }

    /**
     * Updates the custom fostering amount input.
     */
    fun updateCustomFosteringAmount(value: String) {
        _customFosteringAmount.value = value
        _fosteringAmountError.value = false
    }

    /**
     * Confirms the fostering amount and creates the fostering.
     */
    /**
     * Confirms the fostering amount and shows payment mock dialog.
     */
    fun confirmFosteringAmount(animalId: String) {
        val selected = _selectedFosteringAmount.value
        val finalAmount = if (selected == -1.0) {
            _customFosteringAmount.value?.toDoubleOrNull()
        } else {
            selected
        }

        if (finalAmount != null && finalAmount > 0) {
            // Store for later use
            pendingFosteringAnimalId = animalId
            pendingFosteringAmount = finalAmount

            // Generate mock payment data
            _mockPaymentData.value = MockPaymentData(
                accountNumber = generateMockAccountNumber(),
                holderName = generateMockHolderName(),
                cvv = generateMockCvv()
            )

            // Close amount dialog, open payment dialog
            dismissFosteringDialog()
            _showPaymentMockDialog.value = true
        } else {
            _fosteringAmountError.value = true
        }
    }

    /**
     * Confirms the mock payment and creates the fostering.
     */
    fun confirmMockPayment() {
        val animalId = pendingFosteringAnimalId
        val amount = pendingFosteringAmount

        if (animalId != null && amount != null) {
            createFostering(animalId, amount)
            dismissPaymentMockDialog()
        }
    }

    /**
     * Clears fostering result (after showing success/error message).
     */
    fun clearFosteringResult() {
        _fosteringResult.value = null
    }

    /**
     * Creates a fostering for the given animal with the specified amount.
     */
    private fun createFostering(animalId: String, amount: Double) {
        viewModelScope.launch {
            try {
                val result = fosteringRepository.createFostering(animalId, amount)

                if (result.isSuccess) {
                    _fosteringResult.value = Result.success(Unit)
                    // Reload animal to update fostering progress
                    loadAnimal(animalId)
                } else {
                    _fosteringResult.value = Result.failure(
                        result.exceptionOrNull() ?: Exception("Failed to create fostering")
                    )
                }
            } catch (e: Exception) {
                _fosteringResult.value = Result.failure(e)
            }
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

    // ========== MOCK DATA GENERATORS ==========

    private fun generateMockAccountNumber(): String {
        return "${Random.nextInt(1000, 9999)} ${Random.nextInt(1000, 9999)} ${Random.nextInt(1000, 9999)} ${Random.nextInt(1000, 9999)}"
    }

    private fun generateMockHolderName(): String {
        val firstNames = listOf("João", "Maria", "Pedro", "Ana", "Carlos", "Sofia", "Miguel", "Inês")
        val lastNames = listOf("Silva", "Santos", "Costa", "Ferreira", "Rodrigues", "Pereira", "Almeida", "Carvalho")
        return "${firstNames.random()} ${lastNames.random()}"
    }

    private fun generateMockCvv(): String {
        return Random.nextInt(100, 999).toString()
    }

    /**
     * Resets the UI state back to Loading.
     * Useful when retrying after an error.
     */
    fun retry(animalId: String) {
        loadAnimal(animalId)
    }


}