package com.example.seepawandroid.ui.screens.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seepawandroid.data.remote.dtos.auth.ReqRegisterUserDto
import com.example.seepawandroid.data.repositories.AuthRepository
import com.example.seepawandroid.utils.ValidationUtils
import com.example.seepawandroid.utils.ValidationUtils.getBirthDateError
import com.example.seepawandroid.utils.ValidationUtils.getEmailError
import com.example.seepawandroid.utils.ValidationUtils.getNameError
import com.example.seepawandroid.utils.ValidationUtils.getPasswordError
import com.example.seepawandroid.utils.ValidationUtils.getPostalCodeError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the registration screen.
 * Manages registration form state and validation.
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    // Form fields
    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _confirmPassword = MutableLiveData("")
    val confirmPassword: LiveData<String> = _confirmPassword

    private val _confirmPasswordError = MutableLiveData<String?>()
    val confirmPasswordError: LiveData<String?> = _confirmPasswordError

    private val _street = MutableLiveData("")
    val street: LiveData<String> = _street

    private val _city = MutableLiveData("")
    val city: LiveData<String> = _city

    private val _postalCode = MutableLiveData("")
    val postalCode: LiveData<String> = _postalCode

    private val _birthDate = MutableLiveData<LocalDate?>(null)
    val birthDate: LiveData<LocalDate?> = _birthDate

    // Error fields for individual validation
    private val _nameError = MutableLiveData<String?>()
    val nameError: LiveData<String?> = _nameError

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _postalCodeError = MutableLiveData<String?>()
    val postalCodeError: LiveData<String?> = _postalCodeError

    private val _birthDateError = MutableLiveData<String?>()
    val birthDateError: LiveData<String?> = _birthDateError

    // UI State
    private val _uiState = MutableLiveData<RegisterUiState>(RegisterUiState.Idle)
    val uiState: LiveData<RegisterUiState> = _uiState

    // Update functions
    fun updateName(value: String) { _name.value = value }
    fun updateEmail(value: String) { _email.value = value }
    fun updatePassword(value: String) { _password.value = value }
    fun updateStreet(value: String) { _street.value = value }
    fun updateCity(value: String) { _city.value = value }
    fun updatePostalCode(value: String) { _postalCode.value = value }
    fun updateBirthDate(date: LocalDate) { _birthDate.value = date }

    /**
     * Validates all form fields.
     * @return Error message if validation fails, null otherwise
     */
    private fun validateForm(): String? {
        ValidationUtils.getTextFieldError(_street.value ?: "", "Rua")?.let { return it }
        ValidationUtils.getTextFieldError(_city.value ?: "", "Cidade")?.let { return it }
        if (_password.value != _confirmPassword.value) return "As passwords não coincidem"

        return null
    }

    /**
     * Attempts to register a new user account.
     */
    fun register() {
        viewModelScope.launch {
            // Validate
            val validationError = validateForm()
            if (validationError != null) {
                _uiState.value = RegisterUiState.Error(validationError)
                return@launch
            }

            _uiState.value = RegisterUiState.Loading

            // Convert birthdate to YYYY-MM-DD format
            val birthDateIso = _birthDate.value!!.toString() // LocalDate.toString() gives YYYY-MM-DD

            val registerDto = ReqRegisterUserDto(
                name = _name.value!!.trim(),
                birthDate = birthDateIso,
                street = _street.value!!.trim(),
                city = _city.value!!.trim(),
                postalCode = _postalCode.value!!.trim(),
                email = _email.value!!.trim(),
                password = _password.value!!
            )

            val result = repository.register(registerDto)

            if (result.isSuccess) {
                _uiState.value = RegisterUiState.Success
            } else {
                _uiState.value = RegisterUiState.Error(
                    result.exceptionOrNull()?.message ?: "Erro ao criar conta"
                )
            }
        }
    }

    /**
     * Updates the name field value.
     *
     * @param value The new name value
     */
    fun onNameChange(value: String) {
        _name.value = value
    }

    /**
     * Updates the email field value.
     *
     * @param value The new email value
     */
    fun onEmailChange(value: String) {
        _email.value = value
    }

    /**
     * Updates the password field value.
     *
     * @param value The new password value
     */
    fun onPasswordChange(value: String) {
        _password.value = value
    }

    /**
     * Updates the confirm password field value.
     *
     * @param value The new confirm password value
     */
    fun onConfirmPasswordChange(value: String) {
        _confirmPassword.value = value
    }

    /**
     * Updates the street field value.
     *
     * @param value The new street value
     */
    fun onStreetChange(value: String) {
        _street.value = value
    }

    /**
     * Updates the city field value.
     *
     * @param value The new city value
     */
    fun onCityChange(value: String) {
        _city.value = value
    }

    /**
     * Updates the postal code field value.
     *
     * @param value The new postal code value
     */
    fun onPostalCodeChange(value: String) {
        _postalCode.value = value
    }

    /**
     * Updates the birth date field value.
     *
     * @param value The new birth date
     */
    fun onBirthDateChange(value: LocalDate) {
        _birthDate.value = value
    }

    /**
     * Validates the name field and updates error state.
     */
    fun validateName() {
        _nameError.value = getNameError(_name.value ?: "")
    }

    /**
     * Validates the email field and updates error state.
     */
    fun validateEmail() {
        _emailError.value = getEmailError(_email.value ?: "")
    }

    /**
     * Validates the password field and updates error state.
     */
    fun validatePassword() {
        _passwordError.value = getPasswordError(_password.value ?: "")
    }

    /**
     * Validates that passwords match and updates error state.
     */
    fun validateConfirmPassword() {
        _confirmPasswordError.value = if (_password.value != _confirmPassword.value) {
            "As passwords não coincidem"
        } else null
    }

    /**
     * Validates the postal code field and updates error state.
     */
    fun validatePostalCode() {
        _postalCodeError.value = getPostalCodeError(_postalCode.value ?: "")
    }

    /**
     * Validates the birth date field and updates error state.
     */
    fun validateBirthDate() {
        _birthDateError.value = getBirthDateError(_birthDate.value)
    }

    /**
     * Resets the UI state to Idle.
     */
    fun resetUiState() {
        _uiState.value = RegisterUiState.Idle
    }
}