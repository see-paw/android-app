package com.example.seepawandroid.di

import com.example.seepawandroid.data.managers.NotificationManager
import com.example.seepawandroid.data.managers.OwnershipStateManager
import com.example.seepawandroid.data.managers.SessionManager
import com.example.seepawandroid.data.remote.api.services.NotificationService
import com.example.seepawandroid.data.repositories.OwnershipRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides
    @Singleton
    fun provideOwnershipStateManager(
        repository: OwnershipRepository
    ): OwnershipStateManager {
        return OwnershipStateManager(repository)
    }

    @Provides
    @Singleton
    fun provideNotificationService(): NotificationService {
        return NotificationService()
    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        notificationService: NotificationService,
        ownershipStateManager: OwnershipStateManager,
        sessionManager: SessionManager
    ): NotificationManager {
        return NotificationManager(notificationService, ownershipStateManager, sessionManager)
    }
}