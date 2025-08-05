package com.isczaragoza.ualacitieschallenge.infrastructure.workers.city

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import com.isczaragoza.ualacitieschallenge.domain.workers.WorkerRequestProvider
import javax.inject.Inject

class CitySyncWorkerRequestProvider @Inject constructor() :
    WorkerRequestProvider<OneTimeWorkRequest> {
        /**
         * Podemos generar y devolver cualquier tipo de WorkManager, PeriodicWorkRequest, OneTimeWorkRequest,
         * según sea el requerimiento del producto.
         * */
    override fun provideWorker(): OneTimeWorkRequest {
        val request = OneTimeWorkRequestBuilder<CitySyncWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true).build()
            )
            .build()
        return request
    }
}
