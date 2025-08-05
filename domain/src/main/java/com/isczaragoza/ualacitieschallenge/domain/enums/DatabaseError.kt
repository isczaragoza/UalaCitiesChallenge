package com.isczaragoza.ualacitieschallenge.domain.enums

import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.BaseError

enum class DatabaseError(): BaseError {
    INSERT_ERROR,
    SELECT_ERROR,
    UPDATE_ERROR,
    UNKNOWN_ERROR
}