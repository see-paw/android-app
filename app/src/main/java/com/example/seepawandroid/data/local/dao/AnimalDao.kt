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

    /**
     * Retrieves a single animal by its unique ID.
     *
     * @param id The unique identifier of the animal.
     * @return The Animal entity if found, null otherwise.
     */
    @Query("SELECT * FROM animals WHERE id = :id LIMIT 1")
    suspend fun getAnimalById(id: String): Animal?

    /**
     * Inserts or updates a single Animal entity.
     * If an animal with the same ID exists, it will be replaced.
     *
     * @param animal The animal to insert or update.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimal(animal: Animal)
}
