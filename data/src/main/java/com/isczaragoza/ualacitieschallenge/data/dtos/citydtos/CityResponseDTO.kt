package com.isczaragoza.ualacitieschallenge.data.dtos.citydtos

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CityResponseDTO(
    @SerializedName("_id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("coord") val coord: CoordDTO
)
