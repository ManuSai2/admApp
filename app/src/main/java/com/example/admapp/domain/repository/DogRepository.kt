package com.example.admapp.domain.repository

import com.example.admapp.domain.model.Breed
import com.example.admapp.domain.model.BreedImage
import kotlinx.coroutines.flow.Flow

interface DogRepository {
    suspend fun getAllBreeds(): Result<List<Breed>>
    suspend fun getBreedImages(breedName: String): Result<List<BreedImage>>
    suspend fun getRandomBreedImage(breedName: String): Result<BreedImage>
    suspend fun getRandomImage(): Result<BreedImage>
    fun getFavorites(): Flow<List<Breed>>
    fun isFavorite(breedName: String): Flow<Boolean>
    suspend fun saveFavorite(breed: Breed, imageUrl: String)
    suspend fun removeFavorite(breedName: String)
}