package com.isczaragoza.ualacitieschallenge.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.isczaragoza.ualacitieschallenge.data.entities.city.CityEntity
import com.isczaragoza.ualacitieschallenge.data.entities.city.CityEntityFts
import com.isczaragoza.ualacitieschallenge.data.entities.metadataupdate.MetadataUpdateEntity
import com.isczaragoza.ualacitieschallenge.infrastructure.database.city.CityDAO
import com.isczaragoza.ualacitieschallenge.infrastructure.database.metadataupdate.MetadataUpdateDAO

/**
 * Definición de la base de datos con Room.
 * */
@Database(
    entities = [CityEntity::class, CityEntityFts::class, MetadataUpdateEntity::class],
    version = 1,
    exportSchema = false
)
abstract class UalaCitiesChallengeRoomDB : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "UalaCitiesChallengeRoomDatabase"
    }

    abstract fun cityDAO(): CityDAO
    abstract fun metadataUpdateDAO(): MetadataUpdateDAO
}
