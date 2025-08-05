package com.isczaragoza.ualacitieschallenge.domain.usecases

import android.util.Log
import com.isczaragoza.ualacitieschallenge.domain.constants.METADATA_SYNC_CITY_NAME
import com.isczaragoza.ualacitieschallenge.domain.enums.DatabaseError
import com.isczaragoza.ualacitieschallenge.domain.enums.DownloadError
import com.isczaragoza.ualacitieschallenge.domain.enums.SyncError
import com.isczaragoza.ualacitieschallenge.domain.repositories.city.CityRepository
import com.isczaragoza.ualacitieschallenge.domain.repositories.metadataupdate.MetadataUpdateRepository
import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.DownloadResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**Descarga solo cuando se satisface el tiempo establecido a través del parámetro proporcionado.*/
class SyncCitiesByIntervalParameterUseCase @Inject constructor(
    private val metadataUpdateRepository: MetadataUpdateRepository,
    private val cityRepository: CityRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(interval: Long = 60 * 60 * 1000): Flow<DownloadResultWrapper<Void>> {
        return flow {
            val cutoffTime = System.currentTimeMillis() - interval

            val shouldDownload = try {
                val metadata =
                    metadataUpdateRepository.getMetadataUpdateByName(METADATA_SYNC_CITY_NAME)
                when {
                    metadata.isEmpty() -> true
                    metadata.first().lastUpdate <= cutoffTime -> true
                    else -> false
                }
            } catch (e: Exception) {
                Log.e("SyncCitiesByInterval", "Error al obtener metadata: ${e.message}")
                emit(DownloadResultWrapper.Failure(DatabaseError.SELECT_ERROR))
                return@flow
            }

            if (!shouldDownload) {
                emit(DownloadResultWrapper.Success)
                return@flow
            }

            try {
                emitAll(
                    cityRepository.syncCitiesNetworkToDB().onEach { result ->
                        when (result) {
                            is DownloadResultWrapper.Success -> {
                                Log.d("SyncCitiesByInterval", "Insert Cities Success")
                                try {
                                    metadataUpdateRepository.insert(METADATA_SYNC_CITY_NAME)
                                } catch (e: Exception) {
                                    DownloadResultWrapper.Failure(DatabaseError.INSERT_ERROR)
                                    Log.e(
                                        "SyncCitiesByInterval",
                                        "Error al guardar metadata: ${e.message}"
                                    )
                                }
                            }

                            is DownloadResultWrapper.Progress -> {
                                Log.d("SyncCitiesByInterval", "Progreso: $result")
                            }

                            is DownloadResultWrapper.Failure -> {
                                Log.d("SyncCitiesByInterval", "Fallo en sync: $result")
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("SyncCitiesByInterval", "Error general en sync: ${e.message}")
                emit(DownloadResultWrapper.Failure(SyncError.FULL_SYNC_ERROR))
            }
        }.flowOn(Dispatchers.IO)
    }
}
