package com.isczaragoza.ualacitieschallenge.domain.enums

import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.BaseError

enum class DownloadError : BaseError {
    REQUEST_ERROR,
    EMPTY_RESPONSE
}