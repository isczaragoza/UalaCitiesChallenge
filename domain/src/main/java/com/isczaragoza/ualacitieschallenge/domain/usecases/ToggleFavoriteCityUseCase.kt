package com.isczaragoza.ualacitieschallenge.domain.usecases

import com.isczaragoza.ualacitieschallenge.domain.models.city.City
import com.isczaragoza.ualacitieschallenge.domain.repositories.city.CityRepository
import javax.inject.Inject

class ToggleFavoriteCityUseCase @Inject constructor(private val cityRepository: CityRepository) {
    suspend operator fun invoke(city: City) {
        cityRepository.toggleFavoriteCity(city)
    }
}