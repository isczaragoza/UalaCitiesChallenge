package com.isczaragoza.ualacitieschallenge.data.di.city

import com.isczaragoza.ualacitieschallenge.data.repositories.city.CityRepositoryImpl
import com.isczaragoza.ualacitieschallenge.domain.repositories.city.CityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CityRepositoryModule {
    @Binds
    abstract fun bindCityRepository(cityRepositoryImpl: CityRepositoryImpl): CityRepository
}
