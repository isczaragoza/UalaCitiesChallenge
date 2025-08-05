package com.isczaragoza.ualacitieschallenge.domain.resulthandlers

/**Utilicé Void en lugar de "Nothing" porque Hilt no reconoce el tipo Nothing para generar el código*/
sealed interface DownloadResultWrapper<out T> : BaseResult {
    data class Progress(val percentage: Int) : DownloadResultWrapper<Void>
    data object Success : DownloadResultWrapper<Void>
    data class Failure(val baseError: BaseError) : DownloadResultWrapper<Void>
}