package com.william.pokemon.util

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeCall(
    block: suspend () -> T
): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> when (code()) {
        401 -> "API key tidak valid. Periksa POKEMON_API_KEY di local.properties."
        403 -> "Akses API ditolak. Periksa API key atau batas penggunaan API."
        404 -> "Data Pokémon tidak ditemukan."
        429 -> "Terlalu banyak permintaan. Silakan coba lagi nanti."
        else -> "Server bermasalah (kode ${code()})."
    }

    is IOException -> "Tidak ada koneksi internet."

    else -> message ?: "Terjadi kesalahan."
}