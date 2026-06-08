package com.example.admapp.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.admapp.domain.model.Breed
import com.example.admapp.domain.model.BreedImage
import com.example.admapp.domain.repository.DogRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = false,
    val breed: Breed? = null,
    val images: List<BreedImage> = emptyList(),
    val isFavorite: Boolean = false,
    val error: String? = null
)

class DetailViewModel(
    private val repository: DogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadBreed(breedName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.isFavorite(breedName)
                .onEach { isFav -> _uiState.update { it.copy(isFavorite = isFav) } }
                .launchIn(this)

            repository.getBreedImages(breedName)
                .onSuccess { images ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            breed = Breed(name = breedName),
                            images = images.take(20)
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message)
                    }
                }
        }
    }

    fun toggleFavorite() {
        val state = _uiState.value
        val breed = state.breed ?: return
        val imageUrl = state.images.firstOrNull()?.url ?: ""

        viewModelScope.launch {
            if (state.isFavorite) {
                repository.removeFavorite(breed.name)
            } else {
                repository.saveFavorite(breed, imageUrl)
            }
        }
    }

    fun getShareImageUrl(): String =
        _uiState.value.images.firstOrNull()?.url ?: ""
}