package com.example.seepawandroid.utils

import java.time.LocalDate

object ValidationUtils {
    /**
     * Validates user's full name.
     * Must be at least 2 characters long.
     */
    fun isValidName(name: String): Boolean {
        return name.trim().length >= 2
    }

    /**
     * Validates postal code (Portuguese format).
     * Must be in format 0000-000.
     */
    fun isValidPostalCode(postalCode: String): Boolean {
        val postalPattern = "^\\d{4}-\\d{3}$".toRegex()
        return postalCode.matches(postalPattern)
    }

    /**
     * Validates if the birth date corresponds to at least 18 years of age.
     *
     * @param birthDate The birth date to validate
     * @return true if age is 18 or above, false otherwise
     */
    fun isValidBirthDate(birthDate: LocalDate?): Boolean {
        if (birthDate == null) return false
        val today = LocalDate.now()
        val age = java.time.Period.between(birthDate, today).years
        return age >= 18
    }

    /**
     * Gets validation error message for name.
     */
    fun getNameError(name: String): String? {
        return when {
            name.isEmpty() -> "Nome é obrigatório"
            !isValidName(name) -> "Nome deve ter pelo menos 2 caracteres"
            else -> null
        }
    }

    /**
     * Gets validation error message for postal code.
     */
    fun getPostalCodeError(postalCode: String): String? {
        return when {
            postalCode.isEmpty() -> "Código postal é obrigatório"
            !isValidPostalCode(postalCode) -> "Formato inválido (use 0000-000)"
            else -> null
        }
    }

    /**
     * Returns error message for birth date field, or null if valid.
     *
     * @param birthDate The birth date to validate
     * @return Error message in Portuguese, or null if valid
     */
    fun getBirthDateError(birthDate: LocalDate?): String? {
        return if (birthDate == null) {
            "Data de nascimento é obrigatória"
        } else if (!isValidBirthDate(birthDate)) {
            "Idade mínima: 18 anos"
        } else {
            null
        }
    }

    /**
     * Gets validation error message for generic text fields.
     */
    fun getTextFieldError(text: String, fieldName: String): String? {
        return if (text.trim().isEmpty()) {
            "$fieldName é obrigatório"
        } else null
    }

    /**
     * Validates email format.
     */
    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return email.matches(emailPattern)
    }

    /**
     * Validates password meets ASP.NET Identity requirements.
     * Requirements: minimum 8 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character.
     *
     * @param password The password to validate
     * @return true if valid, false otherwise
     */
    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false

        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar
    }

    /**
     * Gets validation error message for email.
     */
    fun getEmailError(email: String): String? {
        return when {
            email.isEmpty() -> "Email é obrigatório"
            !isValidEmail(email) -> "Formato de email inválido"
            else -> null
        }
    }

    /**
     * Returns error message for password field, or null if valid.
     *
     * @param password The password to validate
     * @return Error message in Portuguese, or null if valid
     */
    fun getPasswordError(password: String): String? {
        if (password.isEmpty()) return "Password é obrigatória"
        if (password.length < 8) return "Mínimo 8 caracteres"
        if (!password.any { it.isUpperCase() }) return "Deve conter pelo menos 1 maiúscula"
        if (!password.any { it.isLowerCase() }) return "Deve conter pelo menos 1 minúscula"
        if (!password.any { it.isDigit() }) return "Deve conter pelo menos 1 número"
        if (!password.any { !it.isLetterOrDigit() }) return "Deve conter pelo menos 1 caractere especial (@#$%...)"
        return null
    }
}