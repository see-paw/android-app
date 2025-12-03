package com.example.seepawandroid.di

import android.os.Build
import android.util.Log
import com.example.seepawandroid.data.managers.SessionManager
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

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TAG = "NetworkModule"
    
    private const val USE_AZURE = false
    
    private const val LOCAL_PORT = 5000
    private const val EMULATOR_LOCALHOST = "10.0.2.2"
    private const val DEVICE_LOCALHOST = "localhost"
    
    private val LOCAL_URL = if (isEmulator()) {
        "http://$EMULATOR_LOCALHOST:$LOCAL_PORT/"
    } else {
        "http://$DEVICE_LOCALHOST:$LOCAL_PORT/"
    }
    
    private const val AZURE_URL = "https://seepaw-api-gdhvbkcvckeub9et.francecentral-01.azurewebsites.net/"

    private val BASE_URL = if (USE_AZURE) AZURE_URL else LOCAL_URL

    init {
        Log.i(TAG, "═══════════════════════════════════════════")
        Log.i(TAG, "   NETWORK MODULE CONFIGURATION")
        Log.i(TAG, "═══════════════════════════════════════════")
        Log.i(TAG, "Environment: ${if (USE_AZURE) "AZURE" else "LOCAL"}")
        Log.i(TAG, "Base URL: $BASE_URL")
        Log.i(TAG, "Is Emulator: ${isEmulator()}")
        Log.i(TAG, "Device: ${Build.MODEL}")
        Log.i(TAG, "Manufacturer: ${Build.MANUFACTURER}")
        Log.i(TAG, "═══════════════════════════════════════════")
        
        if (!USE_AZURE) {
            if (isEmulator()) {
                Log.i(TAG, "📱 Emulador detectado → Usando $EMULATOR_LOCALHOST:$LOCAL_PORT")
                Log.i(TAG, "✅ Certifica-te que o backend está a correr em localhost:$LOCAL_PORT")
            } else {
                Log.i(TAG, "📱 Dispositivo real detectado → Usando $DEVICE_LOCALHOST:$LOCAL_PORT")
                Log.i(TAG, "⚠️  Executa antes: adb reverse tcp:$LOCAL_PORT tcp:$LOCAL_PORT")
            }
        }
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

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
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        Log.i(TAG, "🔧 Configurando Retrofit com Base URL: $BASE_URL")
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBackendApiService(retrofit: Retrofit): BackendApiService {
        return retrofit.create(BackendApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBaseUrl(): String {
        return BASE_URL
    }

    private fun isEmulator(): Boolean {
        val isEmulator = (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.FINGERPRINT.contains("sdk_gphone")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK")
                || Build.MODEL.contains("sdk_gphone")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("emulator"))
        
        if (isEmulator) {
            Log.d(TAG, "✓ Emulador detectado por: ${Build.FINGERPRINT}")
        }
        
        return isEmulator
    }
}
