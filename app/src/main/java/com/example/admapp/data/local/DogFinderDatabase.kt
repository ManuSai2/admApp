package com.example.admapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.admapp.data.local.FavoriteBreedDao
import com.example.admapp.data.local.FavoriteBreedEntity

@Database(
    entities = [FavoriteBreedEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DogFinderDatabase : RoomDatabase() {

    abstract fun favoriteBreedDao(): FavoriteBreedDao

    companion object {
        @Volatile private var INSTANCE: DogFinderDatabase? = null

        fun getInstance(context: Context): DogFinderDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DogFinderDatabase::class.java,
                    "dog_finder.db"
                ).build().also { INSTANCE = it }
            }
    }
}