package com.isczaragoza.ualacitieschallenge.data.datasources.city

import androidx.paging.PagingSource
import androidx.sqlite.db.SupportSQLiteQuery
import com.isczaragoza.ualacitieschallenge.data.database.city.CityDBClient
import com.isczaragoza.ualacitieschallenge.data.entities.city.CityEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CityLocalDataSource @Inject constructor(private val cityDBClient: CityDBClient) {
    suspend fun insert(cities: List<CityEntity>) {
        cityDBClient.insert(cities)
    }

    fun findCitiesByNamePagination(
        cityName: String,
        isFavoriteFiltered: Boolean
    ): PagingSource<Int, CityEntity> {
        return cityDBClient.findCitiesByNamePagination(cityName, isFavoriteFiltered)
    }

    fun getTopCities(): Flow<List<CityEntity>> {
        return cityDBClient.getTopCities()
    }

    suspend fun getFavoriteCityIds(): List<Long> {
        return cityDBClient.getFavoriteCityIds()
    }

    fun getCityById(id: Long): Flow<CityEntity?> {
        return cityDBClient.getCityById(id)
    }

    suspend fun updateFavoriteStatus(cityId: Long, isFavorite: Boolean) {
        return cityDBClient.updateFavoriteStatus(cityId, isFavorite)
    }

    suspend fun insertCitiesWithRawQuery(query: SupportSQLiteQuery): Int {
        return cityDBClient.insertCitiesWithRawQuery(query)
    }

    fun findCitiesByName(cityName: String): Flow<List<CityEntity>> {
        return cityDBClient.findCitiesByName(cityName)
    }
}
