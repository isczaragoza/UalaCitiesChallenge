package com.isczaragoza.ualacitieschallenge.data.entities.metadataupdate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "MetadataUpdateEntity"
)
data class MetadataUpdateEntity(
    @PrimaryKey
    val metadataName: String,
    val lastUpdate: Long
)