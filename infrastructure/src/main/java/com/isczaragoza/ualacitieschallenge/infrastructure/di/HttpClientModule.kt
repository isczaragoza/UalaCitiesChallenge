package com.isczaragoza.ualacitieschallenge.infrastructure.di

import com.isczaragoza.ualacitieschallenge.data.network.HttpClientProvider
import com.isczaragoza.ualacitieschallenge.infrastructure.network.RetrofitHttpClientProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HttpClientModule {
    @Binds
    @Singleton
    abstract fun bindsHttpClientProvider(retrofitHttpClientProvider: RetrofitHttpClientProvider): HttpClientProvider<Retrofit>
}
