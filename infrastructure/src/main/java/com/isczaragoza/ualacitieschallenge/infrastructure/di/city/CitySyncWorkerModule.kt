package com.isczaragoza.ualacitieschallenge.infrastructure.di.city

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.DownloadResultWrapper
import com.isczaragoza.ualacitieschallenge.domain.workers.WorkerContracts
import com.isczaragoza.ualacitieschallenge.domain.workers.WorkerManager
import com.isczaragoza.ualacitieschallenge.domain.workers.WorkerRequestProvider
import com.isczaragoza.ualacitieschallenge.infrastructure.workers.city.CitySyncWorkerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow

@Module
@InstallIn(SingletonComponent::class)
object CitySyncWorkerModule {
    @Provides
    fun provideCitySyncWorkerManager(
        workerRequestProvider: WorkerRequestProvider<OneTimeWorkRequest>,
        workerContracts: WorkerContracts<OneTimeWorkRequest, WorkInfo>
    ): WorkerManager<Flow<DownloadResultWrapper<Void>>> {
        return CitySyncWorkerManager(workerRequestProvider, workerContracts)
    }
}
