package com.isczaragoza.ualacitieschallenge.data.dtos.citydtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoordDTO(@SerialName("lat") val lat: Double, @SerialName("lon") val lon: Double)
