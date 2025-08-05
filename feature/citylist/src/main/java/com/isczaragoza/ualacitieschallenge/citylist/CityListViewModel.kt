package com.isczaragoza.ualacitieschallenge.citylist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.isczaragoza.ualacitieschallenge.domain.models.city.City
import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.DownloadResultWrapper
import com.isczaragoza.ualacitieschallenge.domain.usecases.GetCitiesByNamePaginated
import com.isczaragoza.ualacitieschallenge.domain.usecases.GetTopCitiesUseCase
import com.isczaragoza.ualacitieschallenge.domain.usecases.StartSyncCitiesWorkerUseCase
import com.isczaragoza.ualacitieschallenge.domain.usecases.ToggleFavoriteCityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class CityListViewModel @Inject constructor(
    private val startSyncCitiesWorkerUseCase: StartSyncCitiesWorkerUseCase,
    private val getCitiesByNamePaginated: GetCitiesByNamePaginated,
    private val getTopCitiesUseCase: GetTopCitiesUseCase,
    private val toggleFavoriteCityUseCase: ToggleFavoriteCityUseCase,
) : ViewModel() {

    private var syncCitiesJob: Job? = null
    private val _syncCitiesState: MutableStateFlow<SyncCitiesState> =
        MutableStateFlow(SyncCitiesState.Loading(0))
    val syncCitiesState: StateFlow<SyncCitiesState> = _syncCitiesState.asStateFlow()

    private val _isFavoriteFiltered = MutableStateFlow(false)
    val isFavoriteFiltered: StateFlow<Boolean> = _isFavoriteFiltered.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSynchronizing = MutableStateFlow(true)
    val isSynchronizing: StateFlow<Boolean> = _isSynchronizing

    fun startCitySyncOneTimeWorker() {
        if (syncCitiesJob != null) {
            return
        }
        syncCitiesJob = viewModelScope.launch(Dispatchers.IO) {
            startSyncCitiesWorkerUseCase().collect { downloadResultWrapper ->
                when (downloadResultWrapper) {
                    is DownloadResultWrapper.Progress -> {
                        _isSynchronizing.value = true
                        _syncCitiesState.update { SyncCitiesState.Loading(downloadResultWrapper.percentage) }
                    }

                    is DownloadResultWrapper.Success -> {
                        _isSynchronizing.value = false
                        _syncCitiesState.update { SyncCitiesState.Success }
                    }

                    is DownloadResultWrapper.Failure -> {
                        _syncCitiesState.update {
                            _isSynchronizing.value = false
                            SyncCitiesState.LoadFailed(downloadResultWrapper.baseError)
                        }
                    }
                }
            }
        }
    }

    /**
     * Aquí elegí observar los cambios en la BD para mostrar un efecto de live stream en la carga de
     * ciudades para la vista.
     * */
    val cityListPaging = combine(
        searchQuery,
        isFavoriteFiltered,
        isSynchronizing
    ) { query, isFavoriteFiltered, isSynchronizing ->
        Triple(
            query,
            isFavoriteFiltered,
            isSynchronizing
        )
    }
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { (query, isFavoriteFiltered, isSynchronizing) ->
            /**Esta bandera es para no usar los filtros al inicio y optimizar**/
            if (isSynchronizing) {
                getTopCitiesUseCase()
                    .distinctUntilChanged()
                    .debounce(1000)
                    .map {
                        PagingData.from(it)
                    }.flowOn(Dispatchers.Default)
            } else {
                getCitiesByNamePaginated(query, isFavoriteFiltered) as Flow<PagingData<City>>
            }
        }
        .flowOn(Dispatchers.Default)
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = PagingData.empty()
        )

    fun updateSearchQuery(searchQuery: String) {
        _searchQuery.value = searchQuery
    }

    fun toggleFavorite(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            val duration = measureTimeMillis {
                toggleFavoriteCityUseCase(city)
            }
            Log.d("toggleFavoriteCityUseCase", "toggleFavoriteCityUseCase in $duration ms")
        }
    }

    fun filterByFavorites(isFavoriteFiltered: Boolean) {
        _isFavoriteFiltered.value = isFavoriteFiltered.not()
    }

    @VisibleForTesting
    internal fun setSynchronizingForTest(value: Boolean) {
        _isSynchronizing.value = value
    }

    @VisibleForTesting
    internal val cityListPagingForTest = combine(
        searchQuery,
        isFavoriteFiltered,
        isSynchronizing
    ) { query, isFavoriteFiltered, isSynchronizing ->
        Triple(
            query,
            isFavoriteFiltered,
            isSynchronizing
        )
    }
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { (query, isFavoriteFiltered, isSynchronizing) ->
            /**Esta bandera es para no usar los filtros al inicio y optimizar**/
            if (isSynchronizing) {
                getTopCitiesUseCase()
                    .distinctUntilChanged()
                    .debounce(1000)
                    .map {
                        PagingData.from(it)
                    }.flowOn(Dispatchers.Default)
            } else {
                getCitiesByNamePaginated(query, isFavoriteFiltered) as Flow<PagingData<City>>
            }
        }
        .cachedIn(viewModelScope)
}
