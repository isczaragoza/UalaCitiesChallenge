package com.isczaragoza.ualacitieschallenge.data.database

interface DBClientProvider<T> {
    fun provideDBClient(): T
}
