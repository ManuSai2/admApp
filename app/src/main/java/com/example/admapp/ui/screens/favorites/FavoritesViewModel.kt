package com.example.admapp.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admapp.domain.model.Breed
import com.example.admapp.domain.repository.DogRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favorites: List<Breed> = emptyList(),
    val isEmpty: Boolean = false
)

class FavoritesViewModel(
    private val repository: DogRepository
) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> =
        repository.getFavorites()
            .map { breeds -> FavoritesUiState(favorites = breeds, isEmpty = breeds.isEmpty()) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FavoritesUiState()
            )

    fun removeFavorite(breedName: String) {
        viewModelScope.launch {
            repository.removeFavorite(breedName)
        }
    }
}