package com.isczaragoza.ualacitieschallenge.domain.resulthandlers

sealed interface ResultWrapper<out T> : BaseResult {
    data class Success<T>(val data: T) : ResultWrapper<T>
    data class Failure(val baseError: BaseError) : ResultWrapper<Nothing>
}
