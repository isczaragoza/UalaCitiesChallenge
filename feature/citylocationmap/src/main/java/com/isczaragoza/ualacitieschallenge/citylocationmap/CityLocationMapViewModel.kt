package com.isczaragoza.ualacitieschallenge.citylocationmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.ResultWrapper
import com.isczaragoza.ualacitieschallenge.domain.usecases.GetCityByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CityLocationMapViewModel @Inject constructor(private val getCityByIdUseCase: GetCityByIdUseCase) :
    ViewModel() {

    private val _cityId = MutableStateFlow<Long>(-1L)

    fun setCityId(id: Long) {
        _cityId.value = id
    }

    val state: StateFlow<CityLocationMapUiState> = _cityId
        .filter {
            it != -1L
        }
        .flatMapLatest { id ->
            getCityByIdUseCase(id)
        }.map { resultWrapper ->
            when (resultWrapper) {
                is ResultWrapper.Success -> {
                    CityLocationMapUiState.Success(resultWrapper.data)
                }

                is ResultWrapper.Failure -> {
                    CityLocationMapUiState.LoadFailed(resultWrapper.baseError)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CityLocationMapUiState.Loading
        )
}
