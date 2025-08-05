package com.isczaragoza.ualacitieschallenge.data.entities.city

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CityEntity",
    indices = [Index(value = ["name", "country", "id"])]
)
data class CityEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val country: String,
    val isFavorite: Boolean,
    val lat: Double,
    val lon: Double
)
