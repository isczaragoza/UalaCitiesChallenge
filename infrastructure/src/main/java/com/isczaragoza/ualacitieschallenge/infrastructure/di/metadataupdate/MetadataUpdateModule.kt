package com.isczaragoza.ualacitieschallenge.infrastructure.di.metadataupdate

import com.isczaragoza.ualacitieschallenge.data.database.metadataupdate.MetadataUpdateDBClient
import com.isczaragoza.ualacitieschallenge.infrastructure.database.metadataupdate.MetadataUpdateRoomDBClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MetadataUpdateModule {
    @Binds
    abstract fun bindsMetadataUpdateRoomClient(metadataUpdateModule: MetadataUpdateRoomDBClient): MetadataUpdateDBClient
}