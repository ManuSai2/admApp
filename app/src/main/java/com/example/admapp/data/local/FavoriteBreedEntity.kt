package com.example.admapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_breeds")
data class FavoriteBreedEntity(
    @PrimaryKey val breedName: String,
    val subBreeds: String = "",           // CSV: "chocolate,black,golden"
    val savedImageUrl: String = "",
    val savedAt: Long = System.currentTimeMillis()
)