package com.isczaragoza.ualacitieschallenge.domain.repositories.city

import com.isczaragoza.ualacitieschallenge.domain.models.city.City
import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.DownloadResultWrapper
import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.DownloadResultWrapperTest
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    fun syncCitiesNetworkToDB(): Flow<DownloadResultWrapper<Void>>
    fun <T> findCitiesByNamePagination(
        cityName: String,
        isFavoriteFiltered: Boolean,
        pageSize: Int = 25
    ): T
    fun getCityById(id: Long): Flow<City?>
    fun getTopCities(): Flow<List<City>>
    suspend fun toggleFavoriteCity(city: City)
}