package com.isczaragoza.ualacitieschallenge.infrastructure.database.metadataupdate

import com.isczaragoza.ualacitieschallenge.data.database.metadataupdate.MetadataUpdateDBClient
import com.isczaragoza.ualacitieschallenge.data.entities.metadataupdate.MetadataUpdateEntity
import com.isczaragoza.ualacitieschallenge.infrastructure.database.UalaCitiesChallengeRoomDB
import javax.inject.Inject

class MetadataUpdateRoomDBClient @Inject constructor(private val ualaCitiesChallengeRoomDB: UalaCitiesChallengeRoomDB) :
    MetadataUpdateDBClient {
    override suspend fun insert(metadataUpdate: MetadataUpdateEntity) {
        ualaCitiesChallengeRoomDB.metadataUpdateDAO().insert(metadataUpdate)
    }

    override suspend fun getMetadataUpdateByName(metadataName: String): List<MetadataUpdateEntity> {
        return ualaCitiesChallengeRoomDB.metadataUpdateDAO().getMetadataUpdateByName(metadataName)
    }
}
