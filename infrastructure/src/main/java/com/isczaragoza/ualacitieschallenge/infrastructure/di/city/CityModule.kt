package com.isczaragoza.ualacitieschallenge.infrastructure.di.city

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import com.isczaragoza.ualacitieschallenge.data.database.city.CityDBClient
import com.isczaragoza.ualacitieschallenge.data.network.city.CityApiClient
import com.isczaragoza.ualacitieschallenge.domain.workers.WorkerContracts
import com.isczaragoza.ualacitieschallenge.domain.workers.WorkerRequestProvider
import com.isczaragoza.ualacitieschallenge.infrastructure.database.city.CityRoomDBClient
import com.isczaragoza.ualacitieschallenge.infrastructure.network.city.CityRetrofitApiClient
import com.isczaragoza.ualacitieschallenge.infrastructure.workers.city.CitySyncWorkerContractsImpl
import com.isczaragoza.ualacitieschallenge.infrastructure.workers.city.CitySyncWorkerRequestProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CityModule {
    @Binds
    abstract fun bindsCityApiClient(cityRetrofitApiClient: CityRetrofitApiClient): CityApiClient

    @Binds
    abstract fun bindsCityRoomClient(cityRoomDBClient: CityRoomDBClient): CityDBClient

    @Binds
    abstract fun bindsCitySyncWorkerRequestProvider(citySyncWorkerRequestProvider: CitySyncWorkerRequestProvider): WorkerRequestProvider<OneTimeWorkRequest>

    @Binds
    abstract fun bindsCitySyncWorkerContracts(citySyncWorkerContractsImpl: CitySyncWorkerContractsImpl): WorkerContracts<OneTimeWorkRequest, WorkInfo>
}
