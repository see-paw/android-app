package com.example.seepawandroid.data.repositories

import com.example.seepawandroid.data.local.dao.AnimalDao
import com.example.seepawandroid.data.local.entities.Animal
import com.example.seepawandroid.data.models.mappers.toEntity
import javax.inject.Inject
import com.example.seepawandroid.data.remote.api.services.BackendApiService
import com.example.seepawandroid.data.remote.dtos.Animals.AnimalFilterDto
import com.example.seepawandroid.utils.NetworkUtils

class AnimalRepository @Inject constructor(
    private val dao: AnimalDao,
    private val apiService: BackendApiService
) {

    suspend fun getAnimals(
        filters: AnimalFilterDto?,
        sortBy: String?,
        order: String?,
        pageNumber: Int = 1
    ): List<Animal> {

        return try {

            if (!NetworkUtils.isConnected()) {
                // OFFLINE → devolve cache
                return dao.getAll()
            }

            val response = apiService.getAnimals(
                species = filters?.species,
                age = filters?.age,
                size = filters?.size,
                color = filters?.color,
                sex = filters?.sex,
                name = filters?.name,
                shelterName = filters?.shelterName,
                breed = filters?.breed,
                sortBy = sortBy,
                order = order,
                pageNumber = pageNumber
            )

            if (!response.isSuccessful) {
                return dao.getAll() // fallback
            }

            val paged = response.body()
            val entities = paged?.items?.map { it.toEntity() } ?: emptyList()

            // Atualiza cache
            dao.insertAll(entities)

            entities

        } catch (e: Exception) {
            dao.getAll() // offline fallback
        }
    }
}