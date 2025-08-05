package com.isczaragoza.ualacitieschallenge.data.network

interface HttpClientProvider<T> {
    fun provideHttpClient(): T
}