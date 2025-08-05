package com.isczaragoza.ualacitieschallenge.infrastructure

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.isczaragoza.ualacitieschallenge.data.entities.city.CityEntity
import com.isczaragoza.ualacitieschallenge.infrastructure.database.UalaCitiesChallengeRoomDB
import com.isczaragoza.ualacitieschallenge.infrastructure.database.city.CityDAO
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CityDaoTest {

    private lateinit var database: UalaCitiesChallengeRoomDB
    private lateinit var cityDao: CityDAO

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UalaCitiesChallengeRoomDB::class.java
        ).allowMainThreadQueries().build()

        cityDao = database.cityDAO()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun findCitiesByNamePagination_correctResults() = runTest {
        val cities = listOf(
            CityEntity(3, "Tokyo", "Japan", false, 40.4, -3.7),
            CityEntity(25, "Acapulco", "Mexico", true, 35.0, 139.0),
            CityEntity(2, "Madrid", "Spain", true, 35.0, 139.0),
            CityEntity(4, "Zibatah", "Spain", false, 40.4, -3.7),
            CityEntity(98, "Aab", "Japan", true, 35.0, 139.0),
            CityEntity(10, "Belmopan", "Belice", false, 40.4, -3.7),
            CityEntity(100, "Aab", "Japan", true, 35.0, 139.0),
            CityEntity(58, "Andorra", "Spain", false, 40.4, -3.7),
            CityEntity(1, "Zibatah", "Mexico", true, 35.0, 139.0)
        )

        cityDao.insertCities(cities)

        /*val pager = Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                cityDao.findCitiesByNamePagination("A", isFavoriteFiltered = false)
            }
        )*/

        val pagingSource = cityDao.findCitiesByNamePagination("A", isFavoriteFiltered = false)

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val page = loadResult as PagingSource.LoadResult.Page
        val loadedCities = page.data

        val expected = listOf(
            CityEntity(98, "Aab", "Japan", true, 35.0, 139.0),
            CityEntity(100, "Aab", "Japan", true, 35.0, 139.0),
            CityEntity(25, "Acapulco", "Mexico", true, 35.0, 139.0),
            CityEntity(58, "Andorra", "Spain", false, 40.4, -3.7)
        )
        assertEquals(expected, loadedCities)
    }

    /**Implementación de los escenarios planteados en la documentación del reto*/
    @Test
    fun findCitiesByNamePagination_correctResults_prefixA() = runTest {
        val cities = listOf(
            CityEntity(25, "Albuquerque", "US", true, 35.0, 139.0),
            CityEntity(98, "Sydney", "AU", true, 35.0, 139.0),
            CityEntity(3, "Alabama", "US", false, 40.4, -3.7),
            CityEntity(4, "Arizona", "US", false, 40.4, -3.7),
            CityEntity(2, "Anahaim", "US", true, 35.0, 139.0)
        )

        cityDao.insertCities(cities)

        val pagingSource = cityDao.findCitiesByNamePagination("A", isFavoriteFiltered = false)

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val page = loadResult as PagingSource.LoadResult.Page
        val loadedCities = page.data

        val expected = listOf(
            CityEntity(3, "Alabama", "US", false, 40.4, -3.7),
            CityEntity(25, "Albuquerque", "US", true, 35.0, 139.0),
            CityEntity(2, "Anahaim", "US", true, 35.0, 139.0),
            CityEntity(4, "Arizona", "US", false, 40.4, -3.7)
        )
        assertEquals(expected, loadedCities)
    }

    @Test
    fun findCitiesByNamePagination_correctResults_prefixS() = runTest {
        val cities = listOf(
            CityEntity(25, "Albuquerque", "US", true, 35.0, 139.0),
            CityEntity(98, "Sydney", "AU", true, 35.0, 139.0),
            CityEntity(3, "Alabama", "US", false, 40.4, -3.7),
            CityEntity(4, "Arizona", "US", false, 40.4, -3.7),
            CityEntity(2, "Anahaim", "US", true, 35.0, 139.0)
        )

        cityDao.insertCities(cities)

        val pagingSource = cityDao.findCitiesByNamePagination("s", isFavoriteFiltered = false)

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val page = loadResult as PagingSource.LoadResult.Page
        val loadedCities = page.data

        val expected = listOf(
            CityEntity(98, "Sydney", "AU", true, 35.0, 139.0),
        )
        assertEquals(expected, loadedCities)
    }

    @Test
    fun findCitiesByNamePagination_correctResults_prefixAl() = runTest {
        val cities = listOf(
            CityEntity(25, "Albuquerque", "US", true, 35.0, 139.0),
            CityEntity(98, "Sydney", "AU", true, 35.0, 139.0),
            CityEntity(3, "Alabama", "US", false, 40.4, -3.7),
            CityEntity(4, "Arizona", "US", false, 40.4, -3.7),
            CityEntity(2, "Anahaim", "US", true, 35.0, 139.0)
        )

        cityDao.insertCities(cities)

        val pagingSource = cityDao.findCitiesByNamePagination("Al", isFavoriteFiltered = false)

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val page = loadResult as PagingSource.LoadResult.Page
        val loadedCities = page.data

        val expected = listOf(
            CityEntity(3, "Alabama", "US", false, 40.4, -3.7),
            CityEntity(25, "Albuquerque", "US", true, 35.0, 139.0),
        )
        assertEquals(expected, loadedCities)
    }

    @Test
    fun findCitiesByNamePagination_correctResults_prefixAlb() = runTest {
        val cities = listOf(
            CityEntity(25, "Albuquerque", "US", true, 35.0, 139.0),
            CityEntity(98, "Sydney", "AU", true, 35.0, 139.0),
            CityEntity(3, "Alabama", "US", false, 40.4, -3.7),
            CityEntity(4, "Arizona", "US", false, 40.4, -3.7),
            CityEntity(2, "Anahaim", "US", true, 35.0, 139.0)
        )

        cityDao.insertCities(cities)

        val pagingSource = cityDao.findCitiesByNamePagination("Alb", isFavoriteFiltered = false)

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val page = loadResult as PagingSource.LoadResult.Page
        val loadedCities = page.data

        val expected = listOf(
            CityEntity(25, "Albuquerque", "US", true, 35.0, 139.0)
        )
        assertEquals(expected, loadedCities)
    }
}
