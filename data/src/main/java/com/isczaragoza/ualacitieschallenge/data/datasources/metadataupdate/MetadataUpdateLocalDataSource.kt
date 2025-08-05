package com.isczaragoza.ualacitieschallenge.data.datasources.metadataupdate

import com.isczaragoza.ualacitieschallenge.data.database.metadataupdate.MetadataUpdateDBClient
import com.isczaragoza.ualacitieschallenge.data.entities.metadataupdate.MetadataUpdateEntity
import javax.inject.Inject

class MetadataUpdateLocalDataSource @Inject constructor(private val metadataUpdateDBClient: MetadataUpdateDBClient) {
    suspend fun insert(metadataUpdate: MetadataUpdateEntity) {
        metadataUpdateDBClient.insert(metadataUpdate)
    }

    suspend fun getMetadataUpdateByName(metadataName: String): List<MetadataUpdateEntity> {
        return metadataUpdateDBClient.getMetadataUpdateByName(metadataName)
    }
}
