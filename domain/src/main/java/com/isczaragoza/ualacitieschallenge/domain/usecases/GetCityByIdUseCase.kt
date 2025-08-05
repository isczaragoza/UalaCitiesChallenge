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

/*
* class GetListOfDogsUseCase(private val dogRepository: DogRepository) {
    operator fun invoke(): Flow<Result<List<DogEntity>>> {
        return dogRepository.getListOfDogs().map {
            Result.Success(it) as Result<List<DogEntity>>
        }.catch {
            println("Catch ${it.message}")
            println("Catch ${it.cause}")
            println("Catch ${it.stackTrace}")
            println("Catch ${it.localizedMessage}")
            when (it.message?.lowercase()) {
                NetworkError.TIME_OUT.message.lowercase() -> {
                    emit(Result.Error(NetworkError.TIME_OUT))
                }

                NetworkError.NO_NETWORK_CONNECTION.message.lowercase() -> {
                    emit(Result.Error(NetworkError.NO_NETWORK_CONNECTION))
                }

                DatabaseError.INSERT_ERROR.message.lowercase() -> {
                    emit(Result.Error(DatabaseError.INSERT_ERROR))
                }

                DatabaseError.GET_ERROR.message.lowercase() -> {
                    emit(Result.Error(DatabaseError.GET_ERROR))
                }

                else -> {
                    emit(Result.Error(NetworkError.UNKNOWN_ERROR))
                }
            }
        }
    }
}
* */