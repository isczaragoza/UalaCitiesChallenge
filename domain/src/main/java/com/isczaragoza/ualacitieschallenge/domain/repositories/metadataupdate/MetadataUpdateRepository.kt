package com.isczaragoza.ualacitieschallenge.domain.repositories.metadataupdate

import com.isczaragoza.ualacitieschallenge.domain.models.metadataupdate.MetadataUpdate

interface MetadataUpdateRepository {
    suspend fun insert(metadataName: String)

    suspend fun getMetadataUpdateByName(metadataName: String): List<MetadataUpdate>
}