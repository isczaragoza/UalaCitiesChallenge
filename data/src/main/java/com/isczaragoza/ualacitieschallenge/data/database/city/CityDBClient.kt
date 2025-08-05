package com.isczaragoza.ualacitieschallenge.data.database.city

import androidx.paging.PagingSource
import androidx.sqlite.db.SupportSQLiteQuery
import com.isczaragoza.ualacitieschallenge.data.entities.city.CityEntity
import kotlinx.coroutines.flow.Flow

interface CityDBClient {
    suspend fun insert(cities: List<CityEntity>)
    fun findCitiesByNamePagination(
        cityName: String,
        isFavoriteFiltered: Boolean
    ): PagingSource<Int, CityEntity>
    suspend fun getFavoriteCityIds(): List<Long>
    fun getCityById(id: Long): Flow<CityEntity?>
    suspend fun insertCitiesWithRawQuery(query: SupportSQLiteQuery): Int
    suspend fun updateFavoriteStatus(cityId: Long, isFavorite: Boolean)
    fun findCitiesByName(cityName: String): Flow<List<CityEntity>>
    fun getTopCities(): Flow<List<CityEntity>>
}