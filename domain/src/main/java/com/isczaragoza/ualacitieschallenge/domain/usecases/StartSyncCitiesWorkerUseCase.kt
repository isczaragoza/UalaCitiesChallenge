package com.isczaragoza.ualacitieschallenge.domain.usecases

import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.DownloadResultWrapper
import com.isczaragoza.ualacitieschallenge.domain.workers.WorkerManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StartSyncCitiesWorkerUseCase @Inject constructor(private val workerManager: WorkerManager<Flow<DownloadResultWrapper<Void>>>) {
    operator fun invoke(): Flow<DownloadResultWrapper<Void>> {
        return workerManager.startWorker()
    }
}
