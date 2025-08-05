package com.isczaragoza.ualacitieschallenge.infrastructure.network.city

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface CityRetrofitApiService {
    @Streaming
    @GET("/hernan-uala/dce8843a8edbe0b0018b32e137bc2b3a/raw/0996accf70cb0ca0e16f9a99e0ee185fafca7af1/cities.json")
    suspend fun fetchCities(): Response<ResponseBody>
}