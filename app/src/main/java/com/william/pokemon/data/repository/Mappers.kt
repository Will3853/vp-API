package com.william.pokemon.data.repository

import com.william.pokemon.data.dto.PokemonCardDto
import com.william.pokemon.model.PokemonCard



fun PokemonCardDto.toDomain(): PokemonCard {

    val prices = tcgplayer?.prices

    val price = prices?.get("normal")
        ?: prices?.get("holofoil")
        ?: prices?.get("reverseHolofoil")
        ?: prices?.get("1stEditionHolofoil")
        ?: prices?.get("1stEditionNormal")

    return PokemonCard(
        id = id,
        name = name,
        imageUrl = images.large,
        marketPrice = price?.market,
        lowestPrice = price?.low,
        activeListings = null,
        verifiedSellers = null,
        primaryPrinting = "normal",
        cardNumber = number.orEmpty(),
        rarity = rarity.orEmpty(),
        types = types,
        setName = set?.name.orEmpty(),
        illustrator = artist.orEmpty()
    )
}

