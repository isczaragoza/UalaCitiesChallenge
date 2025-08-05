package com.isczaragoza.ualacitieschallenge.citylocationmap

import com.isczaragoza.ualacitieschallenge.domain.models.city.City
import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.BaseError

interface CityLocationMapUiState {
    data object Loading : CityLocationMapUiState
    data class Success(val city: City?) : CityLocationMapUiState
    data class LoadFailed(val error: BaseError) : CityLocationMapUiState
}
