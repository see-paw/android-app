package com.example.seepawandroid.data.providers

import android.content.Context
import android.content.SharedPreferences

/**
 * Singleton object responsible for managing user authentication session data.
 *
 * Uses SharedPreferences to persist the JWT authentication token across app restarts.
 * This allows the user to remain logged in even after closing the app.
 *
 * Must be initialized in Application.onCreate() before use.
 */
object SessionManager {
    /**
     * SharedPreferences instance for storing session data.
     * Lazy-initialized via init() method.
     */
    private lateinit var prefs: SharedPreferences

    /**
     * Name of the SharedPreferences file.
     */
    private const val PREF_NAME = "auth_prefs"

    /**
     * Key used to store the authentication token in SharedPreferences.
     */
    private const val KEY_AUTH_TOKEN = "auth_token"

    /**
     * Key used to store the token expiration time in SharedPreferences.
     */
    private const val KEY_TOKEN_EXPIRATION = "token_expiration"

    /**
     * Initializes the SessionManager with application context.
     *
     * Must be called in Application.onCreate() before any other SessionManager methods.
     *
     * @param context Application context (use applicationContext, not Activity context)
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Saves the JWT authentication token to persistent storage.
     *
     * Called after successful login to store the token for future API requests.
     *
     * @param token JWT token received from the backend API
     */
    fun saveAuthToken(token: String, expiration: String) {
        prefs.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .putString(KEY_TOKEN_EXPIRATION, expiration)
            .apply()
    }

    /**
     * Retrieves the stored JWT authentication token.
     *
     * @return The JWT token string, or null if no token is stored (user not logged in)
     */
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }


    /**
     * Clears all session data, effectively logging the user out.
     *
     * Removes the stored token from SharedPreferences.
     * Should be called when user explicitly logs out.
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }

    /**
     * Checks if a user is currently authenticated.
     *
     * @return true if a valid token exists and if it has not expired, false otherwise
     */
    fun isAuthenticated(): Boolean {
        val token = getAuthToken() ?: return false
        val expiration = prefs.getString(KEY_TOKEN_EXPIRATION, null) ?: return false

        return try {
            val expirationDate = java.time.Instant.parse(expiration)
            java.time.Instant.now().isBefore(expirationDate)
        } catch (e: Exception) {
            false
        }
    }
}