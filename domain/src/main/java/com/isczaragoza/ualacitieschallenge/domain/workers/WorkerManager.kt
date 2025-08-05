package com.isczaragoza.ualacitieschallenge.domain.workers

interface WorkerManager<T> {
    fun startWorker(): T
}
