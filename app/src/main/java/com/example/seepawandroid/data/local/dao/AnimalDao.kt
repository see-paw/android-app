package com.example.seepawandroid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.seepawandroid.data.local.entities.Animal

/**
 * Data Access Object (DAO) for the Animal entity.
 *
 * Defines database operations for storing and retrieving Animal records
 * using Room. The DAO abstracts all SQL queries and ensures type safety.
 */
@Dao
interface AnimalDao {

    /**
     * Retrieves all stored Animal entities from the local Room database.
     *
     * @return A list of all Animal objects.
     */
    @Query("SELECT * FROM animals")
    suspend fun getAll(): List<Animal>

    /**
     * Inserts a list of Animal objects into the database.
     * Existing entries with the same primary key will be replaced.
     *
     * @param list The list of animals to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Animal>)
}
