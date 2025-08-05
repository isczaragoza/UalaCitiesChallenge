package com.isczaragoza.ualacitieschallenge.citylist

import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.BaseError

sealed interface SyncCitiesState {
    data class Loading(val progress: Int) : SyncCitiesState
    data class LoadFailed(val error: BaseError) : SyncCitiesState
    data object Success : SyncCitiesState
}
