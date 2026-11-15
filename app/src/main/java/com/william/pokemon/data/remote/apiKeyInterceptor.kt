package com.william.pokemon.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(
    private val apiKey: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()

        val request = original.newBuilder()
            .addHeader("X-Api-Key", apiKey)
            .build()

        return chain.proceed(request)
    }
}
