package com.d3vly.core.domain.repository

import com.d3vly.core.domain.model.UniversityLoadResult

interface UniversityRepository {
    suspend fun getUniversities(): Result<UniversityLoadResult>
}
