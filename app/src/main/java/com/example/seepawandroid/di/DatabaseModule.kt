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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

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

    @Provides
    fun provideAnimalDao(db: AppDatabase): AnimalDao {
        return db.animalDao()
    }
}
