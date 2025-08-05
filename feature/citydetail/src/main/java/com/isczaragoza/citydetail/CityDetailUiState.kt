package com.isczaragoza.citydetail

import com.isczaragoza.ualacitieschallenge.domain.models.city.City
import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.BaseError

sealed interface CityDetailUiState {
    data object Loading : CityDetailUiState
    data class Success(val city: City?) : CityDetailUiState
    data class LoadFailed(val error: BaseError) : CityDetailUiState
}