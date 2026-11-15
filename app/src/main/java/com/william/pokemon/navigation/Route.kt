package com.william.pokemon.navigation

import kotlinx.serialization.Serializable

@Serializable
data object CardRoute

@Serializable
data class CardDetailRoute(val cardId: String)
