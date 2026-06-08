package com.example.admapp.platform

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.util.Log
import android.net.Uri

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

        val cursor = MatrixCursor(arrayOf("_id", "name", "source"))
        cursor.addRow(arrayOf(1, "favorite_breeds_provider_ready", "Dog Finder"))

        return cursor
    }

    override fun getType(uri: Uri): String {
        return "vnd.android.cursor.dir/vnd.com.example.admapp.favoritebreeds"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d("FavoriteBreedsProvider", "Insert not implemented")
        return null
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.d("FavoriteBreedsProvider", "Delete not implemented")
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.d("FavoriteBreedsProvider", "Update not implemented")
        return 0
    }
}