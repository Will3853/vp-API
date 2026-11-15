
package com.william.pokemon.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.william.pokemon.ui.widgets.ErrorBox
import com.william.pokemon.ui.widgets.LoadingBox
import com.william.pokemon.ui.widgets.PokemonCardItem
import com.william.pokemon.viewmodel.DashboardUiState
import com.william.pokemon.viewmodel.PokemonViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun CardScreen(
    onCardClick: (String) -> Unit,
    viewModel: PokemonViewModel = viewModel(
        factory = PokemonViewModel.Factory
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CardContent(
        state = state,
        onCardClick = onCardClick,
        onSearch = viewModel::searchCards,
        onLoadMore = viewModel::loadNextPage,
        onRetry = viewModel::retryList
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardContent(
    state: DashboardUiState,
    onCardClick: (String) -> Unit,
    onSearch: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
) {
    val gridState = rememberLazyGridState()
    var searchQuery by remember { mutableStateOf("") }

    val suggestions = listOf(
        "Charizard",
        "Pikachu",
        "Mewtwo",
        "Gengar"
    )

    val nearEnd by remember {
        derivedStateOf {
            val info = gridState.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0

            info.totalItemsCount > 0 &&
                    last >= info.totalItemsCount - 4
        }
    }

    LaunchedEffect(
        nearEnd,
        state.cards.size,
        state.isLoadingList,
        state.endReached
    ) {
        if (nearEnd && !state.isLoadingList && !state.endReached) {
            onLoadMore()
        }
    }

    Scaffold { innerPadding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp,
                bottom = 20.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F0F18))
                        .padding(8.dp),

                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.Red),
                            contentAlignment = Alignment.Center
                        ){
                            Icon(Icons.Filled.CatchingPokemon, contentDescription = "", modifier = Modifier.size(30.dp))
                        }
                        Column {
                            Text(
                                text = "PokéTracker",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White
                            )

                            Text(
                                text = "Real-Time TCG Card Valuation",
                                color = Color(0xFF777777),
                                fontSize = 10.sp,

                                )
                        }
                    }

                    Surface(
                        color = Color(0xFF122D14),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "● LIVE API",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            color = Color(0xFF2AD924),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Search Pokémon cards...")
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                    onSearch("")
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch(searchQuery.trim())
                        }
                    )
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(suggestions) { pokemon ->
                        FilterChip(
                            selected = searchQuery == pokemon,
                            onClick = {
                                searchQuery = pokemon
                                onSearch(pokemon)
                            },
                            label = {
                                Text(pokemon, color = Color.White)
                            }
                        )
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Market Results (${state.cards.size})",
                        fontSize = 16.sp,
                        color = Color.White
                    )

                    Text(
                        text = "USD • Near Mint",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }

            if (state.cards.isEmpty() && state.isLoadingList) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LoadingBox()
                }
            }

            if (state.cards.isEmpty() && state.listError != null) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ErrorBox(
                        message = state.listError,
                        onRetry = onRetry
                    )
                }
            }

            if (
                state.cards.isEmpty() &&
                !state.isLoadingList &&
                state.listError == null
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("No Pokémon cards found.")
                }
            }

            items(
                items = state.cards,
                key = { it.id }
            ) { pokemon ->

                PokemonCardItem(
                    pokemon = pokemon,
                    onClick = {
                        onCardClick(pokemon.id)
                    }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                when {
                    state.listError != null && state.cards.isNotEmpty() -> {
                        ErrorBox(
                            message = state.listError,
                            onRetry = onRetry
                        )
                    }

                    state.isLoadingList && state.cards.isNotEmpty() -> {
                        LoadingBox()
                    }

                    state.endReached && state.cards.isNotEmpty() -> {
                        Text(
                            text = "No more cards",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
