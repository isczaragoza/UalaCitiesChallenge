package com.isczaragoza.ualacitieschallenge.domain.workers

import kotlinx.coroutines.flow.Flow

interface WorkerContracts<A, B> {
    fun startWorker(request: A)
    fun getPreviousInstanceWorker(): Flow<List<B>>
}
