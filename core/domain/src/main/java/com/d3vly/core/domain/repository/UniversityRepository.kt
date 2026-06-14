package com.d3vly.core.domain.repository

import com.d3vly.core.domain.model.UniversitiesResult

interface UniversityRepository {
    suspend fun getUniversities(): UniversitiesResult
}
