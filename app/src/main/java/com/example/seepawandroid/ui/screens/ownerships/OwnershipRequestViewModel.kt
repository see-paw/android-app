package com.example.seepawandroid.ui.screens.ownership

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.managers.OwnershipStateManager
import com.example.seepawandroid.data.repositories.AnimalRepository
import com.example.seepawandroid.data.repositories.OwnershipRepository
import com.example.seepawandroid.data.repositories.ShelterRepository
import com.example.seepawandroid.data.repositories.UserRepository
import com.example.seepawandroid.ui.screens.ownerships.OwnershipRequestUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * ViewModel for the Ownership Request wizard flow.
 *
 * Manages a 3-step process:
 * 1. Terms acceptance (with scroll tracking)
 * 2. Mock form with auto-filled data
 * 3. Success confirmation
 *
 * Uses optimized data loading: if animalName and shelterId are passed,
 * skips animal query; otherwise fetches from backend.
 *
 * After successful request creation, updates OwnershipStateManager cache.
 */
@HiltViewModel
class OwnershipRequestViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val userRepository: UserRepository,
    private val shelterRepository: ShelterRepository,
    private val ownershipRepository: OwnershipRepository,
    private val ownershipStateManager: OwnershipStateManager
) : ViewModel() {

    private val _uiState = MutableLiveData<OwnershipRequestUiState>(OwnershipRequestUiState.Loading)
    /**
     * The current state of the ownership request UI.
     */
    val uiState: LiveData<OwnershipRequestUiState> = _uiState

    // Cache for data loaded during the flow
    private var cachedUserName: String = ""
    private var cachedAnimalName: String = ""
    private var cachedShelterName: String = ""

    /**
     * Initializes the ownership request flow.
     *
     * Loads animal data (if not provided) and shows terms screen.
     *
     * @param animalId ID of the animal to request.
     * @param animalName Optional pre-loaded animal name (from navigation).
     * @param shelterId Optional pre-loaded shelter ID (from navigation).
     */
    fun initialize(animalId: String, animalName: String?, shelterId: String?) {
        viewModelScope.launch {
            _uiState.value = OwnershipRequestUiState.Loading

            try {
                // Determine if we need to fetch animal data
                val finalAnimalName: String
                val finalShelterId: String

                if (animalName != null && shelterId != null) {
                    // Optimized: use provided data
                    finalAnimalName = animalName
                    finalShelterId = shelterId
                } else {
                    // Fetch animal data from backend
                    val animalResult = animalRepository.getAnimalById(animalId)
                    if (animalResult.isFailure) {
                        _uiState.value = OwnershipRequestUiState.Error(
                            "Failed to load animal data: ${animalResult.exceptionOrNull()?.message}"
                        )
                        return@launch
                    }

                    val (animal, _) = animalResult.getOrNull()!!
                    finalAnimalName = animal.name
                    finalShelterId = animal.shelterId
                }

                // Cache for later use
                cachedAnimalName = finalAnimalName

                // Fetch shelter name
                val shelterResult = shelterRepository.getShelterById(finalShelterId)
                if (shelterResult.isFailure) {
                    _uiState.value = OwnershipRequestUiState.Error(
                        "Failed to load shelter data: ${shelterResult.exceptionOrNull()?.message}"
                    )
                    return@launch
                }

                cachedShelterName = shelterResult.getOrNull()!!.name

                // Show terms screen
                _uiState.value = OwnershipRequestUiState.ShowingTerms(
                    animalId = animalId,
                    animalName = finalAnimalName,
                    hasScrolledToBottom = false
                )

            } catch (e: Exception) {
                _uiState.value = OwnershipRequestUiState.Error(
                    "Unexpected error: ${e.message}"
                )
            }
        }
    }

    /**
     * Updates scroll state for terms screen.
     * Enables accept button when user scrolls to bottom.
     */
    fun onTermsScrolledToBottom() {
        val currentState = _uiState.value
        if (currentState is OwnershipRequestUiState.ShowingTerms) {
            _uiState.value = currentState.copy(hasScrolledToBottom = true)
        }
    }

    /**
     * Proceeds to form screen after user accepts terms.
     * Generates mock payment data automatically.
     */
    fun acceptTerms() {
        val currentState = _uiState.value
        if (currentState is OwnershipRequestUiState.ShowingTerms) {
            // Generate mock data
            val mockAccountNumber = generateMockAccountNumber()
            val mockHolderName = generateMockHolderName()
            val mockCvv = generateMockCvv()
            val mockCitizenCard = generateMockCitizenCard()
            val mockPassword = "••••••••"

            _uiState.value = OwnershipRequestUiState.ShowingForm(
                animalId = currentState.animalId,
                animalName = currentState.animalName,
                accountNumber = mockAccountNumber,
                holderName = mockHolderName,
                cvv = mockCvv,
                citizenCard = mockCitizenCard,
                password = mockPassword,
                isSubmitting = false
            )
        }
    }

    /**
     * Submits the ownership request to the backend.
     * Fetches user data and creates the request.
     * Updates OwnershipStateManager cache after successful creation.
     */
    fun submitOwnershipRequest() {
        val currentState = _uiState.value
        if (currentState is OwnershipRequestUiState.ShowingForm) {
            viewModelScope.launch {
                // Set submitting state
                _uiState.value = currentState.copy(isSubmitting = true)

                try {
                    // Fetch user data
                    val userResult = userRepository.fetchUserData()
                    if (userResult.isFailure) {
                        _uiState.value = OwnershipRequestUiState.Error(
                            "Failed to fetch user data: ${userResult.exceptionOrNull()?.message}"
                        )
                        return@launch
                    }

                    cachedUserName = userResult.getOrNull()!!.name

                    // Create ownership request
                    val ownershipResult = ownershipRepository.createOwnershipRequest(currentState.animalId)
                    if (ownershipResult.isFailure) {
                        _uiState.value = OwnershipRequestUiState.Error(
                            "Failed to create ownership request: ${ownershipResult.exceptionOrNull()?.message}"
                        )
                        return@launch
                    }

                    val newRequest = ownershipResult.getOrNull()!!

                    ownershipStateManager.addOwnershipRequest(newRequest)

                    // Success!
                    _uiState.value = OwnershipRequestUiState.Success(
                        userName = cachedUserName,
                        animalName = cachedAnimalName,
                        shelterName = cachedShelterName
                    )

                } catch (e: Exception) {
                    _uiState.value = OwnershipRequestUiState.Error(
                        "Unexpected error: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Navigates back from form to terms (if user presses back button).
     */
    fun goBackToTerms() {
        val currentState = _uiState.value
        if (currentState is OwnershipRequestUiState.ShowingForm) {
            _uiState.value = OwnershipRequestUiState.ShowingTerms(
                animalId = currentState.animalId,
                animalName = currentState.animalName,
                hasScrolledToBottom = true // Keep scroll state
            )
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

    private fun generateMockCitizenCard(): String {
        return "${Random.nextInt(10000000, 99999999)}"
    }
}