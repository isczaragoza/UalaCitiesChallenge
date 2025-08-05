package com.isczaragoza.ualacitieschallenge.infrastructure.database.city

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.isczaragoza.ualacitieschallenge.data.entities.city.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cityList: List<CityEntity>)

    @Query("SELECT * FROM CityEntity WHERE name LIKE :cityName || '%' " +
            "AND (:isFavoriteFiltered = 0 OR isFavorite = 1) " +
            "ORDER BY name COLLATE NOCASE ASC, country COLLATE NOCASE ASC, id ASC")
    fun findCitiesByNamePagination(
        cityName: String,
        isFavoriteFiltered: Boolean
    ): PagingSource<Int, CityEntity>

    /**Consulta simple para mostrar el proceso del stream durante las inserciones*/
    @Query("SELECT * FROM CityEntity ORDER BY name ASC, id ASC LIMIT 100")
    fun getTopCities(): Flow<List<CityEntity>>

    @Query("SELECT id FROM CityEntity WHERE isFavorite = 1")
    suspend fun getFavoriteCityIds(): List<Long>

    @Query("SELECT * FROM CityEntity WHERE id = :id")
    fun getCityById(id: Long): Flow<CityEntity?>

    @Query("UPDATE CityEntity SET isFavorite = :isFavorite WHERE id = :cityId")
    suspend fun updateFavoriteStatus(cityId: Long, isFavorite: Boolean)

    @RawQuery
    suspend fun insertCitiesWithRawQuery(query: SupportSQLiteQuery): Int

    /*Quitar*/
    @Query("SELECT * FROM CityEntity WHERE name LIKE :cityName || '%' ORDER BY name COLLATE NOCASE ASC, id ASC LIMIT 25")
    fun findCitiesByName(cityName: String): Flow<List<CityEntity>>

    @Query("SELECT c.* FROM CityEntity c JOIN CityEntityFts fts ON c.id = fts.rowid WHERE fts.name MATCH :cityName || '*' ORDER BY c.name COLLATE NOCASE ASC, c.id ASC LIMIT 25")
    fun findCitiesByNameFts(cityName: String): Flow<List<CityEntity>>
}
