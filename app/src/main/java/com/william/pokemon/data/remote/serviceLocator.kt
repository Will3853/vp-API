package com.william.pokemon.data.remote

import com.william.pokemon.BuildConfig
import com.william.pokemon.data.remote.ApiKeyInterceptor
import com.william.pokemon.data.remote.PokemonApi
import com.william.pokemon.data.repository.PokemonRepository
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

object ServiceLocator {

    private const val BASE_URL = "https://api.pokemontcg.io/v2/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                ApiKeyInterceptor(BuildConfig.POKEMON_API_KEY)
            )
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG)
                        HttpLoggingInterceptor.Level.BASIC
                    else
                        HttpLoggingInterceptor.Level.NONE
                }
            )
            .build()
    }

    private val api: PokemonApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
            .create(PokemonApi::class.java)
    }

    val pokemonRepository: PokemonRepository by lazy {
        PokemonRepository(api)
    }
}
