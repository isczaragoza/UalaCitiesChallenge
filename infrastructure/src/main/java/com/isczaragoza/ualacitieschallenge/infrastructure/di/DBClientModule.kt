package com.isczaragoza.ualacitieschallenge.infrastructure.di

import com.isczaragoza.ualacitieschallenge.data.database.DBClientProvider
import com.isczaragoza.ualacitieschallenge.infrastructure.database.RoomDBClientProvider
import com.isczaragoza.ualacitieschallenge.infrastructure.database.UalaCitiesChallengeRoomDB
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DBClientModule {
    @Binds
    @Singleton
    abstract fun bindsDatabaseClientProvider(roomDBClientProvider: RoomDBClientProvider): DBClientProvider<UalaCitiesChallengeRoomDB>
}
