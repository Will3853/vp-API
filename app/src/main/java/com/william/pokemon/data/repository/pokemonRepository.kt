package com.william.pokemon.data.repository

import com.william.pokemon.BuildConfig
import com.william.pokemon.data.remote.PokemonApi
import com.william.pokemon.util.safeCall
import com.william.pokemon.model.PokemonCard

class PokemonRepository(private val api: PokemonApi) {

    suspend fun getFeatured(): Result<List<PokemonCard>> = guarded {
        api.getCards(page = 1, pageSize = 10)
            .data
            .map { it.toDomain() }
    }

    suspend fun getAllCards(page: Int): Result<List<PokemonCard>> = guarded {
        api.getCards(page = page, pageSize = 20)
            .data
            .map { it.toDomain() }
    }

    suspend fun searchCards(
        name: String,
        page: Int = 1
    ): Result<List<PokemonCard>> = guarded {
        api.searchCards(
            query = "name:$name",
            page = page,
            pageSize = 20
        )
            .data
            .map { it.toDomain() }
    }

    suspend fun getCardDetail(id: String): Result<PokemonCard> = guarded {
        api.getCardDetail(id).data.toDomain()
    }

    private suspend fun <T> guarded(
        block: suspend () -> T
    ): Result<T> {
        if (BuildConfig.POKEMON_API_KEY.isBlank()) {
            return Result.failure(
                IllegalStateException(
                    "POKEMON_API_KEY belum diisi. Tambahkan API key di local.properties lalu sync Gradle."
                )
            )
        }

        return safeCall(block)
    }
}