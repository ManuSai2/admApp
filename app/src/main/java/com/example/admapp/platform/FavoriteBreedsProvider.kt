package com.example.admapp.platform

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import com.example.admapp.data.local.DogFinderDatabase

class FavoriteBreedsProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        Log.d("FavoriteBreedsProvider", "ContentProvider created")
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        Log.d("FavoriteBreedsProvider", "Query executed: $uri")

        val cursor = MatrixCursor(arrayOf("_id", "name", "subBreeds"))

        try {
            val db = DogFinderDatabase.getInstance(context!!)
            val favorites = db.favoriteBreedDao().getAllFavoritesSync()

            favorites.forEachIndexed { index, entity ->
                cursor.addRow(arrayOf(index + 1, entity.breedName, entity.subBreeds))
            }

            Log.d("FavoriteBreedsProvider", "Returning ${favorites.size} favorites")
        } catch (e: Exception) {
            Log.e("FavoriteBreedsProvider", "Error querying favorites: ${e.message}")
        }

        return cursor
    }

    override fun getType(uri: Uri) =
        "vnd.android.cursor.dir/vnd.com.example.admapp.favoritebreeds"

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d("FavoriteBreedsProvider", "Insert not implemented")
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?) = 0
}