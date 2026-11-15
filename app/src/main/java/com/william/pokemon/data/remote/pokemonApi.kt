package com.william.pokemon.data.remote

import com.william.pokemon.data.dto.PokemonDetailResponseDto
import com.william.pokemon.data.dto.PokemonListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApi {

    @GET("cards")
    suspend fun getCards(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PokemonListResponseDto

    @GET("cards")
    suspend fun searchCards(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PokemonListResponseDto

    @GET("cards/{id}")
    suspend fun getCardDetail(
        @Path("id") id: String
    ): PokemonDetailResponseDto
}