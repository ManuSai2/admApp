package com.example.admapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admapp.domain.model.Breed
import com.example.admapp.domain.repository.DogRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val breeds: List<Breed> = emptyList(),
    val filteredBreeds: List<Breed> = emptyList(),
    val breedImages: Map<String, String> = emptyMap(),  // ← nuevo
    val searchQuery: String = "",
    val error: String? = null
)

class HomeViewModel(
    private val repository: DogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadBreeds()
    }

    fun loadBreeds() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getAllBreeds()
                .onSuccess { breeds ->
                    _uiState.update {
                        it.copy(isLoading = false, breeds = breeds, filteredBreeds = breeds)
                    }
                    loadBreedImages(breeds.map { it.name })
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun loadBreedImages(breedNames: List<String>) {
        viewModelScope.launch {
            // Cargamos de a lotes de 10 para no saturar la API
            breedNames.chunked(10).forEach { chunk ->
                chunk.forEach { name ->
                    launch {
                        repository.getRandomBreedImage(name)
                            .onSuccess { image ->
                                _uiState.update { state ->
                                    state.copy(
                                        breedImages = state.breedImages + (name to image.url)
                                    )
                                }
                            }
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) state.breeds
            else state.breeds.filter {
                it.name.contains(query, ignoreCase = true)
            }
            state.copy(searchQuery = query, filteredBreeds = filtered)
        }
    }
}