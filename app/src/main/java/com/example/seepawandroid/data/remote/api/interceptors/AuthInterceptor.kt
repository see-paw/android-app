package com.example.seepawandroid.data.remote.api.interceptors

import com.example.seepawandroid.data.managers.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Intercepts network requests to add an Authorization header if a token is available.
 *
 * @param sessionManager The manager for retrieving the session token.
 */
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    /**
     * Intercepts the outgoing request to add the authentication token to the header.
     *
     * @param chain The interceptor chain.
     * @return The modified request's response.
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