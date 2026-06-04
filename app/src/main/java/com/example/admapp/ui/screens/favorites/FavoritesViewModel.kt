package com.example.admapp.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admapp.domain.model.Breed
import com.example.admapp.domain.repository.DogRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


enum class SortOrder {
    RECENT,      // más recientes primero (orden de guardado, invertido)
    OLDEST,      // más antiguos primero
    A_Z,         // alfabético A→Z
    Z_A          // alfabético Z→A
}

data class FavoritesUiState(
    val favorites: List<Breed> = emptyList(),
    val isEmpty: Boolean = true,
    val sortOrder: SortOrder = SortOrder.RECENT
)

class FavoritesViewModel(
    private val repository: DogRepository
) : ViewModel() {

    private val _sortOrder = MutableStateFlow(SortOrder.RECENT)

    val uiState: StateFlow<FavoritesUiState> =
        combine(
            repository.getFavorites(),
            _sortOrder
        ) { breeds, sort ->
            val sorted = when (sort) {
                SortOrder.RECENT -> breeds
                SortOrder.OLDEST -> breeds.reversed()
                SortOrder.A_Z    -> breeds.sortedBy { it.name }
                SortOrder.Z_A    -> breeds.sortedByDescending { it.name }
            }
            FavoritesUiState(
                favorites = sorted,
                isEmpty = sorted.isEmpty(),
                sortOrder = sort
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FavoritesUiState()
            )

    // Caché de imágenes en memoria: breedName -> imageUrl
    private val _breedImages = MutableStateFlow<Map<String, String>>(emptyMap())
    val breedImages: StateFlow<Map<String, String>> = _breedImages.asStateFlow()

    fun loadImageForBreed(breedName: String) {
        if (_breedImages.value.containsKey(breedName)) return
        viewModelScope.launch {
            // Usamos getRandomBreedImage que ya existe en tu repositorio
            repository.getRandomBreedImage(breedName)
                .onSuccess { breedImage ->
                    _breedImages.update { it + (breedName to breedImage.url) }
                }
            // Si falla (Result.failure) simplemente no se muestra imagen, sin crash
        }
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun removeFavorite(breedName: String) {
        viewModelScope.launch {
            repository.removeFavorite(breedName)
        }
    }
}