package com.isczaragoza.ualacitieschallenge.data.datasources.city

import com.isczaragoza.ualacitieschallenge.data.network.city.CityApiClient
import okhttp3.ResponseBody
import javax.inject.Inject

class CityRemoteDataSource @Inject constructor(private val cityApiClient: CityApiClient) {
    suspend fun fetchCities(): ResponseBody? {
        return cityApiClient.fetchCities()
    }
}
