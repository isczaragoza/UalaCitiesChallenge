package com.isczaragoza.ualacitieschallenge.domain.resulthandlers

interface DownloadResultWrapperTest<out T> : BaseResult {
    data class Progress(val percentage: Int) : DownloadResultWrapperTest<Nothing>
    data class Status<T>(val message: T) : DownloadResultWrapperTest<Nothing>
    data class Partial<T>(val data: T) : DownloadResultWrapperTest<T>
    data class Success<T>(val data: T) : DownloadResultWrapperTest<Nothing>
    data class Failure<T>(val exception: T) : DownloadResultWrapperTest<Nothing>
}
