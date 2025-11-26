package com.example.seepawandroid.data.managers

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import java.time.Instant
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton object responsible for managing user authentication session data.
 *
 * Uses Android Keystore System with AES-GCM encryption to securely persist
 * the authentication token across app restarts.
 * This allows the user to remain logged in even after closing the app.
 *
 * The encryption is done at hardware level (when available) providing maximum security.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
    }

    companion object {
        private const val PREF_NAME = "secure_auth_prefs"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "SeePawAuthKey"

        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_TOKEN_EXPIRATION = "token_expiration"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_ID = "user_id"

        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
    }

    init {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey()
        }
    }

    /**
     * Generates a new AES key in the Android Keystore.
     * The key is stored securely in hardware (when available).
     */
    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    /**
     * Gets the secret key from the Keystore.
     */
    private fun getSecretKey(): SecretKey {
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    /**
     * Encrypts a string using AES-GCM with the key from Android Keystore.
     *
     * @param plainText The text to encrypt
     * @return Base64-encoded string containing IV + encrypted data
     */
    private fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        val combined = iv + encryptedBytes
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    /**
     * Decrypts a string that was encrypted with encrypt().
     *
     * @param encryptedText Base64-encoded string containing IV + encrypted data
     * @return The decrypted plaintext, or null if decryption fails
     */
    private fun decrypt(encryptedText: String): String? {
        return try {
            val combined = Base64.decode(encryptedText, Base64.NO_WRAP)

            val iv = combined.copyOfRange(0, 12)
            val encryptedBytes = combined.copyOfRange(12, combined.size)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves a value encrypted in SharedPreferences.
     */
    private fun saveEncrypted(key: String, value: String) {
        val encrypted = encrypt(value)
        prefs.edit().putString(key, encrypted).apply()
    }

    /**
     * Retrieves and decrypts a value from SharedPreferences.
     */
    private fun getDecrypted(key: String): String? {
        val encrypted = prefs.getString(key, null) ?: return null
        return decrypt(encrypted)
    }

    // ==================== Public API ====================

    /**
     * Saves the JWT authentication token to encrypted persistent storage.
     *
     * @param token JWT token received from the backend API
     * @param expiration Token expiration timestamp
     */
    fun saveAuthToken(token: String, expiration: String) {
        saveEncrypted(KEY_AUTH_TOKEN, token)
        saveEncrypted(KEY_TOKEN_EXPIRATION, expiration)
    }

    /**
     * Retrieves the stored JWT authentication token.
     *
     * @return The JWT token string, or null if no token is stored
     */
    fun getAuthToken(): String? {
        return getDecrypted(KEY_AUTH_TOKEN)
    }

    /**
     * Clears all session data, effectively logging the user out.
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }

    /**
     * Checks if a user is currently authenticated.
     *
     * @return true if a valid token exists and has not expired, false otherwise
     */
    fun isAuthenticated(): Boolean {
        val token = getAuthToken() ?: return false
        val expiration = getDecrypted(KEY_TOKEN_EXPIRATION) ?: return false

        return try {
            val expirationDate = Instant.parse(expiration)
            Instant.now().isBefore(expirationDate)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Retrieves the stored user role.
     */
    fun getUserRole(): String? {
        return getDecrypted(KEY_USER_ROLE)
    }

    /**
     * Saves the authenticated user's role.
     */
    fun saveUserRole(role: String) {
        saveEncrypted(KEY_USER_ROLE, role)
    }

    /**
     * Saves the authenticated user's ID.
     */
    fun saveUserId(userId: String) {
        saveEncrypted(KEY_USER_ID, userId)
    }

    /**
     * Retrieves the stored user ID.
     */
    fun getUserId(): String? {
        return getDecrypted(KEY_USER_ID)
    }
}