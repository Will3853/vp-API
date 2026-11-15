package com.william.pokemon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.william.pokemon.data.remote.ServiceLocator
import com.william.pokemon.data.repository.PokemonRepository
import com.william.pokemon.util.toUserMessage
import com.william.pokemon.model.PokemonCard
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface FeaturedState {
    data object Loading : FeaturedState
    data class Success(val cards: List<PokemonCard>) : FeaturedState
    data class Error(val message: String) : FeaturedState
}


data class DashboardUiState(
    val featured: FeaturedState = FeaturedState.Loading,
    val cards: List<PokemonCard> = emptyList(),
    val isLoadingList: Boolean = false,
    val endReached: Boolean = false,
    val listError: String? = null
)

class PokemonViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var featuredJob: Job? = null
    private var nextPage = 1

    init {
        loadFeatured()
        loadNextPage()
    }

    fun loadFeatured() {
        featuredJob?.cancel()

        _uiState.update {
            it.copy(featured = FeaturedState.Loading)
        }

        featuredJob = viewModelScope.launch {
            repository.getFeatured()
                .onSuccess { cards ->
                    _uiState.update {
                        it.copy(
                            featured = FeaturedState.Success(cards)
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            featured = FeaturedState.Error(
                                error.toUserMessage()
                            )
                        )
                    }
                }
        }
    }

    fun loadNextPage() {
        val state = _uiState.value

        if (
            state.isLoadingList ||
            state.endReached ||
            state.listError != null
        ) return

        _uiState.update {
            it.copy(isLoadingList = true)
        }

        viewModelScope.launch {
            repository.getAllCards(nextPage)
                .onSuccess { cards ->

                    nextPage++

                    _uiState.update {
                        it.copy(
                            cards = (it.cards + cards)
                                .distinctBy { card -> card.id },
                            isLoadingList = false,
                            endReached = cards.size < 20
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingList = false,
                            listError = error.toUserMessage()
                        )
                    }
                }
        }
    }

    fun retryList() {
        _uiState.update {
            it.copy(listError = null)
        }

        loadNextPage()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                PokemonViewModel(
                    ServiceLocator.pokemonRepository
                )
            }
        }
    }
    fun searchCards(query: String) {
        nextPage = 1

        _uiState.update {
            it.copy(
                cards = emptyList(),
                isLoadingList = true,
                endReached = false,
                listError = null
            )
        }

        viewModelScope.launch {
            val result = if (query.isBlank()) {
                repository.getAllCards(1)
            } else {
                repository.searchCards(query, 1)
            }

            result
                .onSuccess { cards ->
                    nextPage = 2

                    _uiState.update {
                        it.copy(
                            cards = cards,
                            isLoadingList = false,
                            endReached = cards.size < 20
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingList = false,
                            listError = error.toUserMessage()
                        )
                    }
                }
        }
    }
}