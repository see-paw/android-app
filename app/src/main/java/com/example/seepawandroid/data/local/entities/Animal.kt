package com.example.seepawandroid.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class Animal(
    @PrimaryKey val id: String,
    val name: String,
    val species: String,
    val size: String,
    val sex: String,
    val breedName: String?,
    val breedId: String?,
    val animalState: String,
    val colour: String,
    val birthDate: String,
    val age: Int,
    val description: String?,
    val sterilized: Boolean,
    val features: String?,
    val cost: Double,
    val shelterId: String,
    val imageUrl: String?
)
