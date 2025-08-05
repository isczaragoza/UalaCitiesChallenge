package com.isczaragoza.ualacitieschallenge.data.database.metadataupdate

import com.isczaragoza.ualacitieschallenge.data.entities.metadataupdate.MetadataUpdateEntity

interface MetadataUpdateDBClient {
    suspend fun insert(metadataUpdate: MetadataUpdateEntity)
    suspend fun getMetadataUpdateByName(metadataName: String): List<MetadataUpdateEntity>
}
