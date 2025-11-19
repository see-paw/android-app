package com.example.seepawandroid.data.providers

import com.example.seepawandroid.data.remote.api.interceptors.AuthInterceptor

import com.example.seepawandroid.data.remote.api.services.BackendApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * Singleton object that provides a configured Retrofit instance for API communication.
 *
 * This is the central HTTP client configuration for the entire app.
 * All API services should use this Retrofit instance to ensure consistent configuration.
 */
object RetrofitInstance {
    /**
     * Base URL of the backend API.
     *
     * - LOCAL: For Android Emulator use 10.0.2.2 (maps to host machine's localhost)
     * - AZURE: Production URL hosted on Azure
     *
     * - USE_AZURE to switch between local development and cloud.
     */
    private const val USE_AZURE = false // Set to "true" to use Azure backend
    private const val LOCAL_URL = "http://10.0.2.2:5000/"
    private const val AZURE_URL = "https://seepaw-api-gdhvbkcvckeub9et.francecentral-01.azurewebsites.net/"

    private val BASE_URL = if (USE_AZURE) AZURE_URL else LOCAL_URL
    private const val BASE_URL = "https://10.0.2.2:5001/api/"

    /**
     * HTTP logging interceptor for debugging network requests.
     *
     * Logs the full request/response body in debug builds.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    // ---------- TRUST ALL SSL (DEV ONLY!) ----------
    private fun getUnsafeOkHttpClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
        )

    /**
     * HTTP Authentication interceptor for adding the JWT token to the header.
     */
    private val authInterceptor = AuthInterceptor {
        SessionManager.getAuthToken()
    }
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

    /**
     * Configured OkHttpClient with logging and timeout settings.
     *
     * - Includes logging interceptor for request/response visibility
     * - 30 second timeout for connect and read operations
     */
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    val api: BackendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getUnsafeOkHttpClient()) // apenas para DEV
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApiService::class.java)
    }
}

    /**
     * Main Retrofit instance configured with:
     * - Base URL pointing to the backend API
     * - Custom OkHttpClient with logging and timeouts
     * - Gson converter for JSON serialization/deserialization
     *
     * Usage example:
     * val apiService = RetrofitInstance.retrofit.create(BackendApiService::class.java)
     */
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}