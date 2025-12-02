package com.example.seepawandroid.di

import com.example.seepawandroid.data.remote.api.interceptors.AuthInterceptor
import com.example.seepawandroid.data.remote.api.services.BackendApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module providing network-related dependencies.
 *
 * Provides Retrofit, OkHttpClient, API services, and interceptors
 * with proper dependency injection lifecycle management.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val LOCAL_URL = "http://10.0.2.2:5000/"
    private const val NGROK_URL = "https://nonmischievous-petulant-rosa.ngrok-free.dev/"
    private const val AZURE_URL = "https://seepaw-api-gdhvbkcvckeub9et.francecentral-01.azurewebsites.net/"

    private val IS_CI = System.getenv("CI") == "true"

    private const val USE_NGROK = true
    private const val USE_AZURE = false

    private val BASE_URL = when {
        IS_CI -> LOCAL_URL
        USE_NGROK -> NGROK_URL
        USE_AZURE -> AZURE_URL
        else -> LOCAL_URL
    }

    /**
     * Provides the logging interceptor for debugging HTTP requests/responses.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Provides the configured OkHttpClient with interceptors.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Provides the Retrofit instance configured for the backend API.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides the BackendApiService for making API calls.
     */
    @Provides
    @Singleton
    fun provideBackendApiService(retrofit: Retrofit): BackendApiService {
        return retrofit.create(BackendApiService::class.java)
    }

    /**
     * Provides the base URL for API and SignalR connections.
     */
    @Provides
    @Singleton
    fun provideBaseUrl(): String {
        return BASE_URL
    }
}