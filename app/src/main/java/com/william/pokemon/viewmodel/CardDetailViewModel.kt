package com.william.pokemon.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.william.pokemon.data.repository.PokemonRepository
import com.william.pokemon.data.remote.ServiceLocator
import com.william.pokemon.model.PokemonCard
import com.william.pokemon.navigation.CardDetailRoute
import com.william.pokemon.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val pokemon: PokemonCard) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PokemonRepository
) : ViewModel() {

    private val pokemonId: String =
        savedStateHandle.toRoute<CardDetailRoute>().cardId

    private val _uiState =
        MutableStateFlow<DetailUiState>(DetailUiState.Loading)

    val uiState: StateFlow<DetailUiState> =
        _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = DetailUiState.Loading

        viewModelScope.launch {
            repository.getCardDetail(pokemonId)
                .onSuccess {
                    _uiState.value = DetailUiState.Success(it)
                }
                .onFailure {
                    _uiState.value = DetailUiState.Error(
                        it.toUserMessage()
                    )
                }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                DetailViewModel(
                    createSavedStateHandle(),
                    ServiceLocator.pokemonRepository
                )
            }
        }
    }
}
