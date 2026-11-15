package com.william.pokemon.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PokemonCardDto(
    val id: String,
    val name: String,
    val number: String? = null,
    val images: PokemonImagesDto,
    val rarity: String? = null,
    val types: List<String> = emptyList(),
    val set: PokemonSetDto? = null,
    val tcgplayer: TcgPlayerDto? = null,
    val cardmarket: CardMarketDto? = null,
    val artist: String? = null
)

@Serializable
data class PokemonImagesDto(
    val small: String,
    val large: String
)
@Serializable
data class PokemonSetDto(
    val name: String? = null
)
@Serializable
data class TcgPlayerDto(
    val prices: Map<String, CardPriceDto>? = null
)
@Serializable
data class CardPriceDto(
    val low: Double? = null,
    val mid: Double? = null,
    val high: Double? = null,
    val market: Double? = null
)

@Serializable
data class CardMarketDto(
    val prices: CardMarketPriceDto? = null
)

@Serializable
data class CardMarketPriceDto(
    val averageSellPrice: Double? = null,
    val lowPrice: Double? = null,
    val trendPrice: Double? = null
)

@Serializable
data class PokemonListResponseDto(
    val data: List<PokemonCardDto>,
    val page: Int? = null,
    val pageSize: Int? = null,
    val count: Int? = null,
    val totalCount: Int? = null
)
@Serializable
data class PokemonDetailResponseDto(
    val data: PokemonCardDto
)