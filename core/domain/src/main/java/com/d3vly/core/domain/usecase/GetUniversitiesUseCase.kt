package com.d3vly.core.domain.usecase

import com.d3vly.core.domain.model.UniversitiesResult
import com.d3vly.core.domain.repository.UniversityRepository
import javax.inject.Inject

class GetUniversitiesUseCase @Inject constructor(
    private val repository: UniversityRepository,
) {
    suspend operator fun invoke(): UniversitiesResult {
        return repository.getUniversities()
    }
}
