package com.isczaragoza.ualacitieschallenge.data.network.city

import okhttp3.ResponseBody

interface CityApiClient {
    suspend fun fetchCities(): ResponseBody?
}