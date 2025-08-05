package com.isczaragoza.ualacitieschallenge.data.entities.city

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

//@Fts4(contentEntity = CityEntity::class)
@Entity(tableName = "CityEntityFts")
data class CityEntityFts(
    @PrimaryKey
    val name: String
)
