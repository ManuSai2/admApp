package com.example.admapp.data.remote

import com.google.gson.annotations.SerializedName

data class BreedListResponse(
    @SerializedName("message") val breeds: Map<String, List<String>>,
    @SerializedName("status") val status: String
)

data class BreedImagesResponse(
    @SerializedName("message") val images: List<String>,
    @SerializedName("status") val status: String
)

data class SingleImageResponse(
    @SerializedName("message") val imageUrl: String,
    @SerializedName("status") val status: String
)
