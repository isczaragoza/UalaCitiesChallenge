package com.isczaragoza.ualacitieschallenge.infrastructure.database.metadataupdate

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.isczaragoza.ualacitieschallenge.data.entities.metadataupdate.MetadataUpdateEntity

@Dao
interface MetadataUpdateDAO {
    @Upsert
    suspend fun insert(metadata: MetadataUpdateEntity)

    @Query("SELECT * FROM MetadataUpdateEntity WHERE metadataName = :metadataName")
    suspend fun getMetadataUpdateByName(metadataName: String): List<MetadataUpdateEntity>
}
