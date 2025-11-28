package com.example.seepawandroid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.seepawandroid.data.local.dao.AnimalDao
import com.example.seepawandroid.data.local.entities.Animal
import com.example.seepawandroid.data.utils.Converters

/**
 * Main Room database for the application.
 *
 * This class defines the local SQLite database schema used by the app.
 * It registers all entities, DAOs, and type converters needed for local storage.
 *
 * Configuration:
 * - entities: List of Room entities stored in the database (currently only Animal).
 * - version: Database version. Must be increased whenever the schema is changed.
 * - exportSchema: When false, Room does not export the schema to a folder.
 *
 * The database uses TypeConverters to allow Room to handle unsupported types,
 * such as List<String> inside the Animal entity.
 */
@Database(
    entities = [Animal::class],
    version = 2, // important: update version when modifying the DB schema
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to all Animal-related database operations.
     *
     * @return The DAO for managing Animal records.
     */
    abstract fun animalDao(): AnimalDao
}
