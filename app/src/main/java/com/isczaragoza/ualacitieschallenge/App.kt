package com.isczaragoza.ualacitieschallenge

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.isczaragoza.ualacitieschallenge.domain.usecases.SyncCitiesByIntervalParameterUseCase
import com.isczaragoza.ualacitieschallenge.infrastructure.workers.city.CitySyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App :
    Application(),
    Configuration.Provider {
    @Inject
    lateinit var workerFactory: CitySyncWorkerFactory
    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setWorkerFactory(workerFactory)
                .build()
}

class CitySyncWorkerFactory
@Inject
constructor(
    private val syncCitiesByIntervalParameterUseCase: SyncCitiesByIntervalParameterUseCase
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = CitySyncWorker(
        appContext,
        workerParameters,
        syncCitiesByIntervalParameterUseCase
    )
}
