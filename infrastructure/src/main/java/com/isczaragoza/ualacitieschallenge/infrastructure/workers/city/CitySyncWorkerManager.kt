package com.isczaragoza.ualacitieschallenge.infrastructure.workers.city

import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import com.isczaragoza.ualacitieschallenge.domain.constants.CITY_SYNC_WORKER_PROGRESS_KEY
import com.isczaragoza.ualacitieschallenge.domain.enums.DatabaseError
import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.DownloadResultWrapper
import com.isczaragoza.ualacitieschallenge.domain.workers.WorkerContracts
import com.isczaragoza.ualacitieschallenge.domain.workers.WorkerManager
import com.isczaragoza.ualacitieschallenge.domain.workers.WorkerRequestProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CitySyncWorkerManager @Inject constructor(
    private val workerRequestProvider: WorkerRequestProvider<OneTimeWorkRequest>,
    private val workerContracts: WorkerContracts<OneTimeWorkRequest, WorkInfo>
) : WorkerManager<Flow<DownloadResultWrapper<Void>>> {
    /**
     * Nos permite observar con Flows y controlar el flujo y progreso de propio worker,
     * la interfaz WorkerManager también genérica nos permite crear nuestras propias
     * implementaciones segun los requermientos del producto.
     * */
    override fun startWorker(): Flow<DownloadResultWrapper<Void>> {
        val request = workerRequestProvider.provideWorker()
        workerContracts.startWorker(request)
        return workerContracts.getPreviousInstanceWorker().map {
            val workers = it
            val className = CitySyncWorker::class.simpleName
            if (className == null) {
                return@map null
            }
            workers.find { workInfo ->
                workInfo.tags.any { tag ->
                    tag.contains(className)
                }
            }
        }.map { workInfo ->
            if (workInfo == null) {
                Log.d("CitySyncWorkerManager", "WorkInfo: $workInfo")
                return@map DownloadResultWrapper.Success
            }
            when (workInfo.state) {
                WorkInfo.State.ENQUEUED -> {
                    Log.d("CitySyncWorkerManager", "WorkInfo.State: ${workInfo.state}")
                    DownloadResultWrapper.Success
                }

                WorkInfo.State.RUNNING -> {
                    Log.d(
                        "CitySyncWorkerManager",
                        "$CITY_SYNC_WORKER_PROGRESS_KEY: ${
                            workInfo.progress.getInt(
                                CITY_SYNC_WORKER_PROGRESS_KEY,
                                0
                            )
                        }"
                    )
                    val progress = workInfo.progress.getInt(CITY_SYNC_WORKER_PROGRESS_KEY, 0)
                    DownloadResultWrapper.Progress(progress)
                }

                WorkInfo.State.SUCCEEDED -> {
                    //PeriodicWorkRequests no entra nunca aquí
                    Log.d("CitySyncWorkerManager", "WorkInfo.State: ${workInfo.state}")
                    DownloadResultWrapper.Success
                }

                WorkInfo.State.FAILED -> {
                    Log.d("CitySyncWorkerManager", "WorkInfo.State: ${workInfo.state}")
                    DownloadResultWrapper.Failure(DatabaseError.INSERT_ERROR)
                }

                WorkInfo.State.BLOCKED -> {
                    Log.d("CitySyncWorkerManager", "WorkInfo.State: ${workInfo.state}")
                    DownloadResultWrapper.Success
                }

                WorkInfo.State.CANCELLED -> {
                    Log.d("CitySyncWorkerManager", "WorkInfo.State: ${workInfo.state}")
                    DownloadResultWrapper.Failure(DatabaseError.INSERT_ERROR)
                }
            }
        }
    }
}
