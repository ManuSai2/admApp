package com.example.admapp.data.local

import androidx.room.*
import com.example.admapp.data.local.FavoriteBreedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteBreedDao {

    @Query("SELECT * FROM favorite_breeds ORDER BY savedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteBreedEntity>>

    @Query("SELECT * FROM favorite_breeds WHERE breedName = :breedName LIMIT 1")
    suspend fun getFavoriteByName(breedName: String): FavoriteBreedEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(breed: FavoriteBreedEntity)

    @Delete
    suspend fun deleteFavorite(breed: FavoriteBreedEntity)

    @Query("DELETE FROM favorite_breeds WHERE breedName = :breedName")
    suspend fun deleteFavoriteByName(breedName: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_breeds WHERE breedName = :breedName)")
    fun isFavorite(breedName: String): Flow<Boolean>
}