package com.isczaragoza.ualacitieschallenge.infrastructure.network

import com.isczaragoza.ualacitieschallenge.data.network.HttpClientProvider
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject

class RetrofitHttpClientProvider @Inject constructor() : HttpClientProvider<Retrofit> {
    private val networkJson: Json = Json { ignoreUnknownKeys = true }
    override fun provideHttpClient(): Retrofit {
        return Retrofit.Builder().baseUrl("https://gist.githubusercontent.com")
            .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
