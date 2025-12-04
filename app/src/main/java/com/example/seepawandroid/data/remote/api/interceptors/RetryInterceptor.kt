package com.example.seepawandroid.data.remote.api.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that retries failed requests due to connection issues.
 *
 * Handles transient network failures like "connection closed" errors
 * that can occur when connections are reused after being closed by the server.
 */
@Singleton
class RetryInterceptor @Inject constructor() : Interceptor {

    companion object {
        private const val TAG = "RetryInterceptor"
        private const val MAX_RETRIES = 3
        private const val RETRY_DELAY_MS = 1000L
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastException: IOException? = null

        repeat(MAX_RETRIES) { attempt ->
            try {
                // Add delay between retries (except first attempt)
                if (attempt > 0) {
                    Log.d(TAG, "Retry attempt ${attempt + 1} for ${request.url}")
                    Thread.sleep(RETRY_DELAY_MS * attempt)
                }

                val response = chain.proceed(request)
                
                // If we get a response, return it
                return response
                
            } catch (e: IOException) {
                lastException = e
                Log.w(TAG, "Request failed (attempt ${attempt + 1}/$MAX_RETRIES): ${e.message}")
                
                // Only retry on connection-related errors
                val shouldRetry = e.message?.let { msg ->
                    msg.contains("connection", ignoreCase = true) ||
                    msg.contains("reset", ignoreCase = true) ||
                    msg.contains("closed", ignoreCase = true) ||
                    msg.contains("timeout", ignoreCase = true) ||
                    msg.contains("refused", ignoreCase = true)
                } ?: false

                if (!shouldRetry) {
                    throw e
                }
            }
        }

        // All retries failed
        throw lastException ?: IOException("Request failed after $MAX_RETRIES retries")
    }
}
