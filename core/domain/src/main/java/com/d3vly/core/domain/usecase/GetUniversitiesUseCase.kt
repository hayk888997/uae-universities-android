package com.d3vly.core.domain.usecase

import com.d3vly.core.domain.model.University
import com.d3vly.core.domain.repository.UniversityRepository
import javax.inject.Inject

class GetUniversitiesUseCase @Inject constructor(
    private val repository: UniversityRepository,
) {
    suspend operator fun invoke(): Result<List<University>> {
        return repository.getUniversities()
    }
}
