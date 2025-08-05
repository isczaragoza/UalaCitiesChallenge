package com.isczaragoza.ualacitieschallenge.domain.usecases

import com.isczaragoza.ualacitieschallenge.domain.enums.DatabaseError
import com.isczaragoza.ualacitieschallenge.domain.models.city.City
import com.isczaragoza.ualacitieschallenge.domain.repositories.city.CityRepository
import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCityByIdUseCase @Inject constructor(private val cityRepository: CityRepository) {
    operator fun invoke(id: Long): Flow<ResultWrapper<City?>> {
        return cityRepository.getCityById(id).map {
            ResultWrapper.Success(it)
        }.catch<ResultWrapper<City?>> {
            println("Select e: $it")
            emit(ResultWrapper.Failure(DatabaseError.SELECT_ERROR))
        }
    }
}
