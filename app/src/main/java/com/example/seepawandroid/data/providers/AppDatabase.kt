package com.example.seepawandroid.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.seepawandroid.data.local.dao.AnimalDao
import com.example.seepawandroid.data.local.entities.Animal


@Database(
    entities = [Animal::class],
    version = 1,//importante alterar versão se mudar algo do schema da BD
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs
    abstract fun animalDao(): AnimalDao

}
