package com.isczaragoza.ualacitieschallenge.domain.models.city

data class City(
    val id: Long,
    val name: String,
    val country: String,
    val isFavorite: Boolean,
    val lat: Double,
    val lon: Double
)
