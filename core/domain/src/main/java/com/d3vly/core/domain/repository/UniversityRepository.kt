package com.d3vly.core.domain.repository

import com.d3vly.core.domain.model.University

interface UniversityRepository {
    suspend fun getUniversities(): Result<List<University>>
}
