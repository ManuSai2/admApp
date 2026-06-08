package com.example.admapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admapp.domain.model.Breed
import com.example.admapp.domain.repository.DogRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOrder { A_Z, Z_A }

data class HomeUiState(
    val isLoading: Boolean = false,
    val breeds: List<Breed> = emptyList(),
    val filteredBreeds: List<Breed> = emptyList(),
    val breedImages: Map<String, String> = emptyMap(),
    val searchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.A_Z,
    val error: String? = null
)

class HomeViewModel(
    private val repository: DogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadBreeds() }

    fun loadBreeds() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getAllBreeds()
                .onSuccess { breeds ->
                    val sorted = breeds.sortedBy { it.name }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            breeds = sorted,
                            filteredBreeds = sorted
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun loadImageIfMissing(breedName: String) {
        if (_uiState.value.breedImages.containsKey(breedName)) return
        viewModelScope.launch {
            repository.getRandomBreedImage(breedName)
                .onSuccess { image ->
                    _uiState.update { state ->
                        state.copy(breedImages = state.breedImages + (breedName to image.url))
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredBreeds = applyFilters(state.breeds, query, state.sortOrder)
            )
        }
    }

    fun setSortOrder(order: SortOrder) {
        _uiState.update { state ->
            state.copy(
                sortOrder = order,
                filteredBreeds = applyFilters(state.breeds, state.searchQuery, order)
            )
        }
    }

    private fun applyFilters(breeds: List<Breed>, query: String, sort: SortOrder): List<Breed> {
        val filtered = if (query.isBlank()) breeds
        else breeds.filter { it.name.contains(query, ignoreCase = true) }
        return when (sort) {
            SortOrder.A_Z -> filtered.sortedBy { it.name }
            SortOrder.Z_A -> filtered.sortedByDescending { it.name }
        }
    }
}