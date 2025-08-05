package com.isczaragoza.ualacitieschallenge.infrastructure.workers.city

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.isczaragoza.ualacitieschallenge.domain.workers.WorkerContracts
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CitySyncWorkerContractsImpl @Inject constructor(@ApplicationContext private val context: Context) :
    WorkerContracts<OneTimeWorkRequest, WorkInfo> {
    /**En este escenario para este tipo de Worker en especifico CitySyncWorker la manera en que
     * se inicia la request ya está protegica con KEEP para evitar ejecuciones simultaneas del mismo
     * Worker (Esto depende de los requerimientos especificos de la feature o trabajo a realizar),
     * pero como buena práctica además de proteger con KEEP tambien observamos el estado en el ViewModel
     * (con nuestro metodo getPreviousInstanceWorker() ) para tener un buen control.
     * El parámetro puede recibir cualquer tipo de request, por ejemplo: PeriodicWorkRequest,
     * si creas la implementación especifica del WorkerContracts con tu tipo de Worker.*/
    override fun startWorker(request: OneTimeWorkRequest) {
        WorkManager.Companion.getInstance(context)
            .enqueueUniqueWork("city_sync_unique", ExistingWorkPolicy.KEEP, request)
    }

    /***
     * Devuelve la información del Worker a través de su Nombre, para acceder a su Informacion y estado
     * con un Flow.
     */
    override fun getPreviousInstanceWorker(): Flow<List<WorkInfo>> {
        return WorkManager.Companion.getInstance(context).getWorkInfosForUniqueWorkFlow("city_sync_unique")
    }
}
