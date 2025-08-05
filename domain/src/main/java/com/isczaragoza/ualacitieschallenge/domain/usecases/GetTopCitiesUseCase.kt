package com.isczaragoza.ualacitieschallenge.domain.usecases

import com.isczaragoza.ualacitieschallenge.domain.models.city.City
import com.isczaragoza.ualacitieschallenge.domain.repositories.city.CityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopCitiesUseCase @Inject constructor(private val cityRepository: CityRepository) {
    operator fun invoke(): Flow<List<City>> {
        return cityRepository.getTopCities()
    }
}