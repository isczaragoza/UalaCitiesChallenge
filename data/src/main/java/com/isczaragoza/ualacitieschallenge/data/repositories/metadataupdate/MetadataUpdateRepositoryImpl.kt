package com.isczaragoza.ualacitieschallenge.data.repositories.metadataupdate

import com.isczaragoza.ualacitieschallenge.data.datasources.metadataupdate.MetadataUpdateLocalDataSource
import com.isczaragoza.ualacitieschallenge.data.entities.metadataupdate.MetadataUpdateEntity
import com.isczaragoza.ualacitieschallenge.data.mappers.metadataupdate.asDomainModel
import com.isczaragoza.ualacitieschallenge.domain.models.metadataupdate.MetadataUpdate
import com.isczaragoza.ualacitieschallenge.domain.repositories.metadataupdate.MetadataUpdateRepository
import javax.inject.Inject

class MetadataUpdateRepositoryImpl @Inject constructor(private val metadataUpdateLocalDataSource: MetadataUpdateLocalDataSource) :
    MetadataUpdateRepository {

    override suspend fun insert(metadataName: String) {
        val metadataUpdateEntity =
            metadataUpdateLocalDataSource.getMetadataUpdateByName(metadataName).firstOrNull()
        if (metadataUpdateEntity != null) {
            metadataUpdateLocalDataSource.insert(metadataUpdateEntity.copy(lastUpdate = System.currentTimeMillis()))
            return
        }
        metadataUpdateLocalDataSource.insert(
            MetadataUpdateEntity(
                metadataName = metadataName,
                lastUpdate = System.currentTimeMillis()
            )
        )
    }

    override suspend fun getMetadataUpdateByName(metadataName: String): List<MetadataUpdate> {
        return metadataUpdateLocalDataSource.getMetadataUpdateByName(metadataName).map {
            it.asDomainModel()
        }
    }
}
