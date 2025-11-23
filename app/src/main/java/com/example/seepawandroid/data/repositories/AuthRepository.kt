package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.providers.SessionManager
import com.example.seepawandroid.data.remote.dtos.auth.ReqLoginDto
import com.example.seepawandroid.data.remote.dtos.auth.ResLoginDto
import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.google.gson.Gson
import retrofit2.Response
import javax.inject.Inject

/**
 * Repository responsible for authentication operations.
 *
 * Abstracts the logic for communicating with the authentication API endpoints.
 * Handles token storage and provides a clean interface for ViewModels.
 */
class AuthRepository @Inject constructor(
    private val apiService: BackendApiService,
    private val sessionManager: SessionManager
){
    private val gson = Gson()

    /**
     * Authenticates a user with email and password.
     *
     * On success, stores the JWT token in SessionManager for future API requests.
     * On failure, returns specific error messages based on backend response.
     *
     * @param email User's email address
     * @param password User's password
     * @return Result object containing login response or specific error message
     */
    suspend fun login(email: String, password: String): Result<ResLoginDto> {
        return try {
            val response = apiService.login(ReqLoginDto(email, password))

            if (response.isSuccessful) {
                val loginData = response.body()
                if (loginData == null) {
                    return Result.failure(Exception("Unexpected server response"))
                }

                // Calculate expiration time from expiresIn (seconds)
                val expirationTime = java.time.Instant.now()
                    .plusSeconds(loginData.expiresIn.toLong())
                    .toString()

                sessionManager.saveAuthToken(loginData.accessToken, expirationTime)
                return Result.success(loginData)
            }

            // Handle error response
            val errorMessage = parseErrorMessage(response)
            Result.failure(Exception(errorMessage))

        } catch (e: Exception) {
            // Handle network/connection errors with consistent messages
            val message = when {
                e.message?.contains("Unable to resolve host") == true ->
                    "Sem conexão à internet"

                e.message?.contains("timeout") == true ->
                    "Tempo de conexão esgotado. Tente novamente"

                else ->
                    "Erro de conexão. Verifique a sua ligação"
            }

            Result.failure(Exception(message))
        }
    }

    /**
     * Parses error response from the backend API.
     *
     * Returns consistent error messages in Portuguese for the user,
     * regardless of backend response variations.
     *
     * @param response Failed HTTP response from the backend
     * @return Human-readable error message in Portuguese
     */
    private fun parseErrorMessage(response: Response<ResLoginDto>): String {
        return when (response.code()) {
            400 -> "Pedido inválido. Verifique os dados inseridos"
            401 -> "Credenciais inválidas. Insira credenciais válidas"
            in 500..599 -> "Servidor indisponível. Tente novamente mais tarde"
            else -> "Erro no login. Tente novamente"
        }
    }
}