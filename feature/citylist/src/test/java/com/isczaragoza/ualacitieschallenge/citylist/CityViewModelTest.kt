package com.isczaragoza.ualacitieschallenge.citylist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.isczaragoza.ualacitieschallenge.domain.models.city.City
import com.isczaragoza.ualacitieschallenge.domain.usecases.GetCitiesByNamePaginated
import com.isczaragoza.ualacitieschallenge.domain.usecases.GetTopCitiesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import kotlin.test.assertEquals
import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.isczaragoza.ualacitieschallenge.domain.usecases.StartSyncCitiesWorkerUseCase
import com.isczaragoza.ualacitieschallenge.domain.usecases.ToggleFavoriteCityUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CityViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var startSyncCitiesWorkerUseCase: StartSyncCitiesWorkerUseCase
    private lateinit var getCitiesByNamePaginated: GetCitiesByNamePaginated
    private lateinit var getTopCitiesUseCase: GetTopCitiesUseCase
    private lateinit var toggleFavoriteCityUseCase: ToggleFavoriteCityUseCase

    private lateinit var viewModel: CityListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        startSyncCitiesWorkerUseCase = mockk()
        getCitiesByNamePaginated = mockk()
        getTopCitiesUseCase = mockk()
        toggleFavoriteCityUseCase = mockk()

        coEvery {
            getCitiesByNamePaginated.invoke<Flow<PagingData<City>>>(any(), any())
        } answers {
            val query = it.invocation.args[0] as String
            val mockedCities = listOf(
                City(
                    1,
                    name = query,
                    country = "TestLand",
                    isFavorite = false,
                    lat = 0.0,
                    lon = 0.0
                )
            )
            flowOf(PagingData.from(mockedCities))
        }

        coEvery {
            getTopCitiesUseCase()
        } returns flowOf(
            listOf(
                City(100, "Aab", "Japan", true, 35.0, 139.0),
                City(25, "Acapulco", "Mexico", true, 35.0, 139.0),
                City(58, "Andorra", "Spain", false, 40.4, -3.7),
                City(10, "Belmopan", "Belice", false, 40.4, -3.7),
                City(2, "Madrid", "Spain", true, 35.0, 139.0),
                City(3, "Tokyo", "Japan", false, 40.4, -3.7),
                City(1, "Zibatah", "Mexico", true, 35.0, 139.0),
                City(4, "Zibatah", "Spain", false, 40.4, -3.7)
            )
        )

        viewModel = CityListViewModel(
            startSyncCitiesWorkerUseCase = startSyncCitiesWorkerUseCase,
            getCitiesByNamePaginated = getCitiesByNamePaginated,
            getTopCitiesUseCase = getTopCitiesUseCase,
            toggleFavoriteCityUseCase = toggleFavoriteCityUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cityListPaging emits filtered cities on searchQuery`() = runTest {
        viewModel.updateSearchQuery("A")
        advanceTimeBy(400)

        val pagingData = viewModel.cityListPagingForTest.first()
        val cities = pagingData.collectData(testDispatcher, this)

        assertEquals(8, cities.size)
        assertEquals("Aab", cities[0].name)
        assertEquals("Acapulco", cities[1].name)
        assertEquals("Andorra", cities[2].name)
    }

    @Test
    fun `cityListPaging emits top cities when synchronizing is true`() = runTest {
        viewModel.setSynchronizingForTest(true)
        advanceTimeBy(1100) // supera debounce de 1000ms
        runCurrent()

        val pagingData = viewModel.cityListPagingForTest.first()
        val cities = pagingData.collectData(testDispatcher, this)

        val nameAndCountryPairs = cities.map { it.name to it.country }
        assertEquals(
            listOf(
                "Aab" to "Japan",
                "Acapulco" to "Mexico",
                "Andorra" to "Spain",
                "Belmopan" to "Belice",
                "Madrid" to "Spain",
                "Tokyo" to "Japan",
                "Zibatah" to "Mexico",
                "Zibatah" to "Spain"
            ),
            nameAndCountryPairs
        )
    }

    @Test
    fun `cityListPaging emits top cities when synchronizing is true using turbine`() = runTest {
        viewModel.setSynchronizingForTest(true)
        advanceUntilIdle()

        viewModel.cityListPagingForTest.test {
            val pagingData = awaitItem()
            val cities = pagingData.collectData(testDispatcher, this@runTest)

            val nameAndCountryPairs = cities.map { it.name to it.country }
            assertEquals(
                listOf(
                    "Aab" to "Japan",
                    "Acapulco" to "Mexico",
                    "Andorra" to "Spain",
                    "Belmopan" to "Belice",
                    "Madrid" to "Spain",
                    "Tokyo" to "Japan",
                    "Zibatah" to "Mexico",
                    "Zibatah" to "Spain"
                ),
                nameAndCountryPairs
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T : Any> PagingData<T>.collectData(
    dispatcher: CoroutineDispatcher,
    scope: TestScope
): List<T> {
    val differ = AsyncPagingDataDiffer(
        diffCallback = object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem
            override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
        },
        updateCallback = NoopListCallback(),
        mainDispatcher = dispatcher,
        workerDispatcher = dispatcher
    )

    differ.submitData(this)
    scope.advanceUntilIdle()
    return differ.snapshot().items
}

class NoopListCallback : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}