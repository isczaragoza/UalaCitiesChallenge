package com.isczaragoza.ualacitieschallenge.infrastructure.di

import com.isczaragoza.ualacitieschallenge.infrastructure.network.RetrofitHttpClientProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    @Singleton
    fun providesRetrofit(retrofitHttpClientProvider: RetrofitHttpClientProvider): Retrofit {
        return retrofitHttpClientProvider.provideHttpClient()
    }
}
