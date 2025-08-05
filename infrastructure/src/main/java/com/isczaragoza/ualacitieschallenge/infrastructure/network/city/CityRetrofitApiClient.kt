package com.isczaragoza.ualacitieschallenge.infrastructure.network.city

import com.isczaragoza.ualacitieschallenge.data.network.city.CityApiClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import javax.inject.Inject

class CityRetrofitApiClient @Inject constructor(
    private val retrofit: Retrofit
) : CityApiClient {
    private val cityRetrofitApiService = retrofit.create(CityRetrofitApiService::class.java)

    override suspend fun fetchCities(): ResponseBody? {
        return cityRetrofitApiService.fetchCities().body()
    }
}
