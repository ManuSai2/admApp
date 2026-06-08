package com.example.admapp.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admapp.domain.model.Breed
import com.example.admapp.domain.repository.DogRepository
import com.example.admapp.ui.screens.config.SettingsViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOrder { RECENT, OLDEST, A_Z, Z_A }

data class FavoritesUiState(
    val favorites: List<Breed> = emptyList(),
    val isEmpty: Boolean = true,
    val sortOrder: SortOrder = SortOrder.RECENT
)

class FavoritesViewModel(
    private val repository: DogRepository,
    private val settingsViewModel: SettingsViewModel
) : ViewModel() {

    private val _sortOrder = MutableStateFlow(SortOrder.RECENT)

    val uiState: StateFlow<FavoritesUiState> =
        combine(repository.getFavorites(), _sortOrder) { breeds, sort ->
            val sorted = when (sort) {
                SortOrder.RECENT -> breeds
                SortOrder.OLDEST -> breeds.reversed()
                SortOrder.A_Z    -> breeds.sortedBy { it.name }
                SortOrder.Z_A    -> breeds.sortedByDescending { it.name }
            }
            FavoritesUiState(favorites = sorted, isEmpty = sorted.isEmpty(), sortOrder = sort)
        }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesUiState())

    private val _breedImages = MutableStateFlow<Map<String, String>>(emptyMap())
    val breedImages: StateFlow<Map<String, String>> = _breedImages.asStateFlow()

    init {
        viewModelScope.launch {
            settingsViewModel.imageCacheVersion
                .drop(1)
                .collect { _breedImages.value = emptyMap() }
        }
    }

    fun loadImageForBreed(breedName: String) {
        if (_breedImages.value.containsKey(breedName)) return
        viewModelScope.launch {
            repository.getRandomBreedImage(breedName)
                .onSuccess { breedImage ->
                    _breedImages.update { it + (breedName to breedImage.url) }
                }
        }
    }

    fun setSortOrder(order: SortOrder) { _sortOrder.value = order }

    fun removeFavorite(breedName: String) {
        viewModelScope.launch { repository.removeFavorite(breedName) }
    }

    fun clearImageCache() {
        _breedImages.value = emptyMap()
    }
}