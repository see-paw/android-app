//package com.example.seepawandroid.data.providers
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//
//
//@Database(
//    //entities = [Animal::class],
//    version = 1,//importante alterar versão se mudar algo do schema da BD
//    exportSchema = false
//)
//abstract class AppDatabase : RoomDatabase() {
//    // DAOs
//    //abstract fun animalDao(): AnimalDao
//
//    companion object {
//
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "seepaw_database"
//                )
//                    .fallbackToDestructiveMigration() // Apaga e recria se mudar versão
//                    .build()
//
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}