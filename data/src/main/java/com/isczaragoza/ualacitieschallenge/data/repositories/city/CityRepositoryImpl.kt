package com.isczaragoza.ualacitieschallenge.data.repositories.city

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.isczaragoza.ualacitieschallenge.data.datasources.city.CityLocalDataSource
import com.isczaragoza.ualacitieschallenge.data.datasources.city.CityRemoteDataSource
import com.isczaragoza.ualacitieschallenge.data.dtos.citydtos.CityResponseDTO
import com.isczaragoza.ualacitieschallenge.data.entities.city.CityEntity
import com.isczaragoza.ualacitieschallenge.data.mappers.city.asDomainModel
import com.isczaragoza.ualacitieschallenge.data.mappers.city.asEntity
import com.isczaragoza.ualacitieschallenge.domain.enums.DatabaseError
import com.isczaragoza.ualacitieschallenge.domain.enums.DownloadError
import com.isczaragoza.ualacitieschallenge.domain.models.city.City
import com.isczaragoza.ualacitieschallenge.domain.repositories.city.CityRepository
import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.DownloadResultWrapper
import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.DownloadResultWrapperTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.internal.EMPTY_RESPONSE
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext
import kotlin.system.measureTimeMillis

class CityRepositoryImpl @Inject constructor(
    private val cityRemoteDataSource: CityRemoteDataSource,
    private val cityLocalDataSource: CityLocalDataSource
) : CityRepository {

    /**Solución de Streaming implementada para descargar grandes volumenes de información, en
     * este escenario intento descargar el JSON de 200k aproximadamente de una forma ooptima,
     * probé multiples enfoques pero este fue le maás óptimo, ya que no cargamos nada en memoria
     * ni manejamos largas listas de elementos. En un inicio  intenté abordar un enfoque de
     * pasar la Lista de Ciudades a una estructura de datos mas optima y manejable, pero
     * aun así se tiene un HashMap de mas de 200k registro y no es óptimo, entonces la descarga
     * por partes/stream y parseo progresivo, con insert, por lotes es el enfoque por el que
     * me decidí... Mas iformación: Revisar README.*/
    override fun syncCitiesNetworkToDB(): Flow<DownloadResultWrapper<Void>> {
        return flow {
            val gson = Gson()
            val batchSize = 1000
            val bufferForDb = mutableListOf<CityEntity>()
            var totalBytesRead = 0L
            var lastProgress = -1
            val insertionDispatcher = Dispatchers.IO.limitedParallelism(4)

            val favoriteIds = try {
                cityLocalDataSource.getFavoriteCityIds().toSet()
            } catch (e: Exception) {
                Log.d("SyncCitiesNetworkToDB", "Error al Get Fav Ids: ${e.message}")
                emit(DownloadResultWrapper.Failure(DatabaseError.SELECT_ERROR))
                return@flow
            }

            val responseBody = try {
                cityRemoteDataSource.fetchCities()
            } catch (e: Exception) {
                Log.d("SyncCitiesNetworkToDB", "Error Request from Stream: ${e.message}")
                emit(DownloadResultWrapper.Failure(DownloadError.REQUEST_ERROR))
                return@flow
            } ?: run {
                emit(DownloadResultWrapper.Failure(DownloadError.EMPTY_RESPONSE))
                return@flow
            }

            val contentLength = responseBody.contentLength().takeIf { it > 0 }

            try {
                coroutineScope {
                    val insertionTasks = mutableListOf<Deferred<Unit>>()
                    var totalBatches = 0
                    responseBody.byteStream().buffered(DEFAULT_BUFFER_SIZE).use { stream ->
                        val countingStream = object : InputStream() {
                            override fun read(): Int {
                                val byte = stream.read()
                                if (byte != -1) totalBytesRead++
                                return byte
                            }

                            override fun read(b: ByteArray, off: Int, len: Int): Int {
                                val read = stream.read(b, off, len)
                                if (read > 0) totalBytesRead += read
                                return read
                            }

                            override fun close() {
                                stream.close()
                            }
                        }

                        InputStreamReader(countingStream, Charsets.UTF_8).use { inputStreamReader ->
                            JsonReader(inputStreamReader).use { jsonReader ->
                                try {
                                    jsonReader.beginArray()

                                    while (jsonReader.hasNext()) {
                                        ensureActive()

                                        val cityDTO = try {
                                            withContext(Dispatchers.Default) {
                                                gson.fromJson<CityResponseDTO>(
                                                    jsonReader,
                                                    CityResponseDTO::class.java
                                                )
                                            }
                                        } catch (e: Exception) {
                                            Log.d(
                                                "SyncCitiesNetworkToDB",
                                                "Error parseando ciudad desde JSON: ${e.message}"
                                            )
                                            throw Exception(
                                                "Error parseando ciudad desde JSON: ${e.message}",
                                                e
                                            )
                                        }

                                        val entity = cityDTO.asEntity()
                                            .copy(isFavorite = cityDTO.id in favoriteIds)
                                        bufferForDb.add(entity)

                                        if (bufferForDb.size >= batchSize) {
                                            val batchToInsert = ArrayList(bufferForDb)
                                            bufferForDb.clear()
                                            totalBatches++

                                            val task = async(insertionDispatcher) {
                                                try {
                                                    cityLocalDataSource.insert(batchToInsert)
                                                } catch (e: Exception) {
                                                    Log.d(
                                                        "SyncCitiesNetworkToDB",
                                                        "Error insertando lote en DB: ${e.message}"
                                                    )
                                                    throw Exception(
                                                        "Error insertando lote en DB: ${e.message}",
                                                        e
                                                    )
                                                }
                                            }
                                            insertionTasks.add(task)
                                        }

                                        contentLength?.let { length ->
                                            val downloadProgress =
                                                ((totalBytesRead * 100) / length).toInt()
                                            val scaledProgress = (downloadProgress * 0.8).toInt()
                                            if (scaledProgress != lastProgress) {
                                                emit(DownloadResultWrapper.Progress(scaledProgress))
                                                lastProgress = scaledProgress
                                            }
                                        }
                                    }

                                    jsonReader.endArray()

                                    //Si hay data en el ultimo lote
                                    if (bufferForDb.isNotEmpty()) {
                                        val batchToInsert = ArrayList(bufferForDb)
                                        bufferForDb.clear()
                                        totalBatches++

                                        val task = async(insertionDispatcher) {
                                            try {
                                                cityLocalDataSource.insert(batchToInsert)
                                            } catch (e: Exception) {
                                                Log.d(
                                                    "SyncCitiesNetworkToDB",
                                                    "Error insertando lote final en DB: ${e.message}"
                                                )
                                                throw Exception(
                                                    "Error insertando lote final en DB: ${e.message}",
                                                    e
                                                )
                                            }
                                        }
                                        insertionTasks.add(task)
                                    }

                                    // Esperar todos los insert
                                    insertionTasks.forEachIndexed { index, task ->
                                        try {
                                            task.await()
                                            val insertionProgress =
                                                ((index + 1) * 100 / totalBatches)
                                            val scaledProgress =
                                                80 + ((insertionProgress * 0.2).toInt())
                                            if (scaledProgress != lastProgress) {
                                                emit(DownloadResultWrapper.Progress(scaledProgress))
                                                lastProgress = scaledProgress
                                            }
                                        } catch (e: Exception) {
                                            Log.d(
                                                "SyncCitiesNetworkToDB",
                                                "Error task await de inserción $index: ${e.message}"
                                            )
                                            throw Exception(
                                                "Error task await de inserción $index: ${e.message}",
                                                e
                                            )
                                        }
                                    }

                                } catch (e: Exception) {
                                    // Limpieza si algo falla en parseando o insertando
                                    bufferForDb.clear()
                                    insertionTasks.forEach { it.cancel() }
                                    throw e
                                }
                            }
                        }
                    }
                }
                emit(DownloadResultWrapper.Success)
            } catch (e: Exception) {
                Log.d("SyncCitiesNetworkToDB", "Error durante el sync, $${e.message}")
                emit(DownloadResultWrapper.Failure(DatabaseError.INSERT_ERROR))
            }
        }.flowOn(Dispatchers.Default)
    }

    /*En el viewmodel debe castearse a Flow<PagingData<City>>*/
    /** Para mantener un enfoque y respetar los principios de arquitectura limpia decidí que el
     * dominio no debe conocer la implementación de la libreria Paging 3, entonces el repositorio
     * pasa un tipo genérico al useCase y este a su vez pasa generico al ViewModel, en donde debe
     * parserse, entonces así mantenemos desacoplado el dominio de implementaciones concretas.
     * */
    @Suppress("UNCHECKED_CAST")
    override fun <T> findCitiesByNamePagination(
        cityName: String,
        isFavoriteFiltered: Boolean,
        pageSize: Int
    ): T {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = {
                cityLocalDataSource.findCitiesByNamePagination(
                    cityName,
                    isFavoriteFiltered
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { cityEntity ->
                cityEntity.asDomainModel()
            }
        }.flowOn(Dispatchers.Default) as T
    }

    override fun getCityById(id: Long): Flow<City?> {
        return cityLocalDataSource.getCityById(id).map {
            it?.asDomainModel()
        }.flowOn(Dispatchers.IO)
    }

    override fun getTopCities(): Flow<List<City>> {
        return cityLocalDataSource.getTopCities().map {
            it.map { cityEntity ->
                cityEntity.asDomainModel()
            }
        }.flowOn(Dispatchers.Default)
    }

    override suspend fun toggleFavoriteCity(city: City) {
        cityLocalDataSource.updateFavoriteStatus(city.id, !city.isFavorite)
    }

    /*Custom query para inserts mas óptimos, no decidí continuar por esta vía porque tiene
       muchas limitaciones de soporte de SQLite respecto a otras BD*/
    /**
     * La finalidad de este Query era evitar tener que traer una lista de ids de las ciudades
     * que han sido marcadas como favoritas convertirla en HashMap y posteriormente validar
     * el nuevo elemento respecto a este mapa, aunque con HashMap es la forma mas optima
     * de ser compatible con el query, sería mucho mejor.*/
    private fun buildPreserveFavoriteInsert(cities: List<CityEntity>): SupportSQLiteQuery {
        val sql = StringBuilder()
        val args = mutableListOf<Any>()

        sql.append("INSERT INTO CityEntity (id, name, country, lat, lon, isFavorite) VALUES ")

        cities.forEachIndexed { index, city ->
            if (index != 0) sql.append(", ")
            sql.append("(?, ?, ?, ?, ?, (SELECT COALESCE(isFavorite, 0) FROM CityEntity WHERE id = ?))")

            args.add(city.id)
            args.add(city.name)
            args.add(city.country)
            args.add(city.lat)
            args.add(city.lon)
            args.add(city.id) // Para el subquery de isFavorite
        }

        sql.append(" ON CONFLICT(id) DO UPDATE SET ")
        sql.append("name = excluded.name, country = excluded.country, lat = excluded.lat, lon = excluded.lon")

        return SimpleSQLiteQuery(sql.toString(), args.toTypedArray())
    }
}
