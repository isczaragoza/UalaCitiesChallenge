package com.isczaragoza.ualacitieschallenge.infrastructure.database.city

import androidx.paging.PagingSource
import androidx.sqlite.db.SupportSQLiteQuery
import com.isczaragoza.ualacitieschallenge.data.database.city.CityDBClient
import com.isczaragoza.ualacitieschallenge.data.entities.city.CityEntity
import com.isczaragoza.ualacitieschallenge.infrastructure.database.UalaCitiesChallengeRoomDB
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CityRoomDBClient @Inject constructor(private val ualaCitiesChallengeRoomDB: UalaCitiesChallengeRoomDB) :
    CityDBClient {

    override suspend fun insert(cities: List<CityEntity>) {
        ualaCitiesChallengeRoomDB.cityDAO().insertCities(cities)
    }

    override fun findCitiesByNamePagination(
        cityName: String,
        isFavoriteFiltered: Boolean
    ): PagingSource<Int, CityEntity> {
        return ualaCitiesChallengeRoomDB.cityDAO()
            .findCitiesByNamePagination(cityName, isFavoriteFiltered)
    }

    override fun getTopCities(): Flow<List<CityEntity>> {
        return ualaCitiesChallengeRoomDB.cityDAO().getTopCities()
    }

    override suspend fun getFavoriteCityIds(): List<Long> {
        return ualaCitiesChallengeRoomDB.cityDAO().getFavoriteCityIds()
    }

    override fun getCityById(id: Long): Flow<CityEntity?> {
        return ualaCitiesChallengeRoomDB.cityDAO().getCityById(id)
    }

    override suspend fun updateFavoriteStatus(cityId: Long, isFavorite: Boolean) {
        return ualaCitiesChallengeRoomDB.cityDAO().updateFavoriteStatus(cityId, isFavorite)
    }

    override suspend fun insertCitiesWithRawQuery(query: SupportSQLiteQuery): Int {
        return ualaCitiesChallengeRoomDB.cityDAO().insertCitiesWithRawQuery(query)
    }

    override fun findCitiesByName(cityName: String): Flow<List<CityEntity>> {
        return ualaCitiesChallengeRoomDB.cityDAO().findCitiesByName(cityName)
    }
}
