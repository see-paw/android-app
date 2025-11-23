package com.example.seepawandroid.di

import android.content.Context
import androidx.room.Room
import com.example.seepawandroid.data.local.AppDatabase
import com.example.seepawandroid.data.local.dao.AnimalDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module responsible for providing database-related dependencies.
 *
 * This module:
 * - Creates and provides the Room database instance
 * - Provides DAO instances
 * - Ensures the database is scoped as a Singleton
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the Room database instance for the entire application.
     *
     * Configuration:
     * - Uses "seepaw_database" as the database name
     * - fallbackToDestructiveMigration(): wipes and rebuilds DB on schema mismatch
     *
     * @param context Application context injected by Hilt.
     * @return A singleton [AppDatabase] instance.
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "seepaw_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Provides the DAO for accessing Animal-related operations.
     *
     * @param db The Room database instance.
     * @return The [AnimalDao] used to access the animals table.
     */
    @Provides
    fun provideAnimalDao(db: AppDatabase): AnimalDao {
        return db.animalDao()
    }
}
