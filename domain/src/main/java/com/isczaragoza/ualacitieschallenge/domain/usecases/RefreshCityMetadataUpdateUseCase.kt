package com.isczaragoza.ualacitieschallenge.domain.usecases

import com.isczaragoza.ualacitieschallenge.domain.constants.METADATA_SYNC_CITY_NAME
import com.isczaragoza.ualacitieschallenge.domain.repositories.metadataupdate.MetadataUpdateRepository
import javax.inject.Inject

class RefreshCityMetadataUpdateUseCase @Inject constructor(private val metadataUpdateRepository: MetadataUpdateRepository) {
    suspend operator fun invoke() {
     metadataUpdateRepository.insert(METADATA_SYNC_CITY_NAME)
    }
}
