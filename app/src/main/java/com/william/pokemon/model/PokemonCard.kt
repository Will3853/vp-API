package com.william.pokemon.model


data class PokemonCard(
    val id: String,
    val name: String,
    val imageUrl: String,
    val marketPrice: Double?,
    val lowestPrice: Double?,
    val activeListings: Int? = null,
    val verifiedSellers: Int? = null,
    val primaryPrinting: String? = null,
    val cardNumber: String,
    val rarity: String,
    val types: List<String>,
    val setName: String,
    val illustrator: String
)
