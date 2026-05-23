package com.example.admapp.data.repository

import com.example.admapp.data.local.FavoriteBreedDao
import com.example.admapp.data.local.FavoriteBreedEntity
import com.example.admapp.data.remote.DogApiService
import com.example.admapp.domain.model.Breed
import com.example.admapp.domain.model.BreedImage
import com.example.admapp.domain.repository.DogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DogRepositoryImpl(
    private val api: DogApiService,
    private val dao: FavoriteBreedDao
) : DogRepository {

    override suspend fun getAllBreeds(): Result<List<Breed>> = runCatching {
        val response = api.getAllBreeds()
        response.breeds.map { (name, subBreeds) ->
            Breed(name = name, subBreeds = subBreeds)
        }.sortedBy { it.name }
    }

    override suspend fun getBreedImages(breedName: String): Result<List<BreedImage>> = runCatching {
        api.getBreedImages(breedName).images.map { url ->
            BreedImage(url = url, breedName = breedName)
        }
    }

    override suspend fun getRandomBreedImage(breedName: String): Result<BreedImage> = runCatching {
        val url = api.getRandomBreedImage(breedName).imageUrl
        BreedImage(url = url, breedName = breedName)
    }

    override suspend fun getRandomImage(): Result<BreedImage> = runCatching {
        val url = api.getRandomImage().imageUrl
        // Extract breed name from URL: .../breeds/labrador-retriever/...
        val breedName = url.substringAfter("breeds/").substringBefore("/")
        BreedImage(url = url, breedName = breedName)
    }

    override fun getFavorites(): Flow<List<Breed>> =
        dao.getAllFavorites().map { entities ->
            entities.map { entity ->
                Breed(
                    name = entity.breedName,
                    subBreeds = if (entity.subBreeds.isBlank()) emptyList()
                    else entity.subBreeds.split(",")
                )
            }
        }

    override fun isFavorite(breedName: String): Flow<Boolean> =
        dao.isFavorite(breedName)

    override suspend fun saveFavorite(breed: Breed, imageUrl: String) {
        dao.insertFavorite(
            FavoriteBreedEntity(
                breedName = breed.name,
                subBreeds = breed.subBreeds.joinToString(","),
                savedImageUrl = imageUrl
            )
        )
    }

    override suspend fun removeFavorite(breedName: String) {
        dao.deleteFavoriteByName(breedName)
    }
}