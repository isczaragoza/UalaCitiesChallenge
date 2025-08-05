package com.isczaragoza.ualacitieschallenge.domain.usecases

import com.isczaragoza.ualacitieschallenge.domain.repositories.city.CityRepository
import javax.inject.Inject

class GetCitiesByNamePaginated @Inject constructor(private val cityRepository: CityRepository) {
    /**En el ViewModel de invocación debe castearse a Flow<PagingData<City>>
     * ya que este Use Case está en la capa de Dominio y
     * no debe tener conocimiento de la librería de Paging que es de la capa Infra*/
    operator fun <T> invoke(cityName: String, isFavoriteFiltered: Boolean): T {
        return cityRepository.findCitiesByNamePagination(cityName, isFavoriteFiltered)
    }
}
