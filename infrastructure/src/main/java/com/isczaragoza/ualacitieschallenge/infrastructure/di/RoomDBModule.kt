package com.isczaragoza.ualacitieschallenge.infrastructure.di

import com.isczaragoza.ualacitieschallenge.infrastructure.database.RoomDBClientProvider
import com.isczaragoza.ualacitieschallenge.infrastructure.database.UalaCitiesChallengeRoomDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDBModule {
    @Provides
    @Singleton
    fun providesRoomDB(roomDBClientProvider: RoomDBClientProvider): UalaCitiesChallengeRoomDB {
        return roomDBClientProvider.provideDBClient()
    }
}
