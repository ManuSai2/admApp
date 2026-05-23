package com.example.admapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface DogApiService {

    @GET("breeds/list/all")
    suspend fun getAllBreeds(): BreedListResponse

    @GET("breed/{breed}/images")
    suspend fun getBreedImages(
        @Path("breed") breed: String
    ): BreedImagesResponse

    @GET("breed/{breed}/images/random")
    suspend fun getRandomBreedImage(
        @Path("breed") breed: String
    ): SingleImageResponse

    @GET("breed/{breed}/{subBreed}/images")
    suspend fun getSubBreedImages(
        @Path("breed") breed: String,
        @Path("subBreed") subBreed: String
    ): BreedImagesResponse

    @GET("breeds/image/random")
    suspend fun getRandomImage(): SingleImageResponse
}