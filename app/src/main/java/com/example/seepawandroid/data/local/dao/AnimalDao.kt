package com.example.seepawandroid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.seepawandroid.data.local.entities.Animal

@Dao
interface AnimalDao {

    @Query("SELECT * FROM animals")
    suspend fun getAll(): List<Animal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Animal>)
}
