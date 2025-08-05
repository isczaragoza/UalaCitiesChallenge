package com.isczaragoza.ualacitieschallenge.domain.workers

interface WorkerRequestProvider<T> {
    fun provideWorker(): T
}
