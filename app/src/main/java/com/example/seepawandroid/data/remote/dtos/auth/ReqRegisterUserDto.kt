package com.example.seepawandroid.data.remote.dtos.auth

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for user registration request.
 *
 * This DTO is sent to the POST /api/Account/register endpoint to create
 * a new user account. Only supports "User" role registration (not AdminCAA).
 */
data class ReqRegisterUserDto(
    /**
     * Full name of the user.
     */
    val name: String,

    /**
     * Birth date in ISO 8601 format (YYYY-MM-DD).
     */
    @SerializedName("birthDate")
    val birthDate: String,

    /**
     * Street address.
     */
    val street: String,

    /**
     * City name.
     */
    val city: String,

    /**
     * Portuguese postal code (XXXX-XXX format).
     */
    val postalCode: String,

    /**
     * Email address.
     */
    val email: String,

    /**
     * Password (must meet ASP.NET Identity requirements).
     */
    val password: String,

    /**
     * User role. Always "User" for mobile registration.
     */
    val selectedRole: String = "User"
)