package com.isczaragoza.ualacitieschallenge.data.di.metadataupdate

import com.isczaragoza.ualacitieschallenge.data.repositories.metadataupdate.MetadataUpdateRepositoryImpl
import com.isczaragoza.ualacitieschallenge.domain.repositories.metadataupdate.MetadataUpdateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MetadataUpdateRepositoryModule {
    @Binds
    abstract fun bindMetadataUpdateRepository(metadataUpdateRepositoryImpl: MetadataUpdateRepositoryImpl): MetadataUpdateRepository
}