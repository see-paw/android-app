package com.example.seepawandroid.data.remote.api.interceptors

import com.example.seepawandroid.data.providers.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * HTTP interceptor that automatically adds JWT authentication token to all requests.
 *
 * This interceptor checks if a token exists and adds it to the Authorization header
 * before sending the request to the backend. This eliminates the need to manually
 * add the token to every API call.
 *
 * The token is retrieved via a lambda function (tokenProvider) to ensure we always
 * get the most recent token value.
 *
 * @property sessionManager SessionManager that provides the current JWT token
 */
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    /**
     * Intercepts the HTTP request and adds authentication header if token exists.
     *
     * Flow:
     * 1. Retrieve current token via tokenProvider
     * 2. If token exists, add "Authorization: Bearer {token}" header
     * 3. If no token, proceed with original request
     * 4. Continue with the request chain
     *
     * @param chain The interceptor chain
     * @return The HTTP response after processing
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionManager.getAuthToken()

        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}