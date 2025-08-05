package com.isczaragoza.ualacitieschallenge.data.mappers.city

import com.isczaragoza.ualacitieschallenge.data.dtos.citydtos.CityResponseDTO
import com.isczaragoza.ualacitieschallenge.data.entities.city.CityEntity
import com.isczaragoza.ualacitieschallenge.domain.models.city.City

fun CityResponseDTO.asEntity(): CityEntity {
    return CityEntity(
        id = id,
        name = name,
        country = country,
        isFavorite = false,
        lat = coord.lat,
        lon = coord.lon
    )
}

fun CityEntity.asDomainModel(): City {
    return City(
        id = id,
        name = name,
        country = country,
        isFavorite = isFavorite,
        lat = lat,
        lon = lon
    )
}
