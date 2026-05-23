package com.example.admapp.domain.model

data class Breed(
    val name: String,
    val subBreeds: List<String> = emptyList()
)