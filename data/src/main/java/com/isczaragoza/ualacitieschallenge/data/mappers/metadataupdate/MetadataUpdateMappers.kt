package com.isczaragoza.ualacitieschallenge.data.mappers.metadataupdate

import com.isczaragoza.ualacitieschallenge.data.entities.metadataupdate.MetadataUpdateEntity
import com.isczaragoza.ualacitieschallenge.domain.models.metadataupdate.MetadataUpdate

fun MetadataUpdateEntity.asDomainModel(): MetadataUpdate {
    return MetadataUpdate(metadataName = metadataName, lastUpdate = lastUpdate)
}

fun MetadataUpdate.asUpdateEntity(newLastUpdate: Long): MetadataUpdateEntity {
    return MetadataUpdateEntity(metadataName = metadataName, lastUpdate = newLastUpdate)
}