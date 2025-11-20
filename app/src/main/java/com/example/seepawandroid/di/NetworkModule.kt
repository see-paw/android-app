package com.example.seepawandroid.di

import com.example.seepawandroid.data.providers.RetrofitInstance
import com.example.seepawandroid.data.remote.api.services.BackendApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton





@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit() = RetrofitInstance.retrofit

    @Provides
    @Singleton
    fun provideBackendApi(): BackendApiService = RetrofitInstance.api
}
