package com.example.admapp

import org.junit.Assert.assertEquals
import org.junit.Test

class BreedSearchTest {

    @Test
    fun filterBreedsByName_returnsMatchingBreeds() {

        val breeds = listOf(
            "akita",
            "beagle",
            "boxer",
            "dalmatian"
        )

        val result = breeds.filter {
            it.contains("bea", ignoreCase = true)
        }

        assertEquals(
            listOf("beagle"),
            result
        )
    }

    @Test
    fun filterBreedsByName_returnsEmptyListWhenNoMatch() {

        val breeds = listOf(
            "akita",
            "beagle",
            "boxer",
            "dalmatian"
        )

        val result = breeds.filter {
            it.contains("poodle", ignoreCase = true)
        }

        assertEquals(
            emptyList<String>(),
            result
        )
    }
}