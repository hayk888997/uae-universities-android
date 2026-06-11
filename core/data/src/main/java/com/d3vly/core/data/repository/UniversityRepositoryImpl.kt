package com.d3vly.core.data.repository

import com.d3vly.core.data.local.UniversityDao
import com.d3vly.core.data.mapper.toDomain
import com.d3vly.core.data.mapper.toEntity
import com.d3vly.core.data.remote.UniversityApi
import com.d3vly.core.domain.model.University
import com.d3vly.core.domain.repository.UniversityRepository
import javax.inject.Inject

class UniversityRepositoryImpl @Inject constructor(
    private val api: UniversityApi,
    private val dao: UniversityDao,
) : UniversityRepository {
    override suspend fun getUniversities(): Result<List<University>> {
        return runCatching {
            val remoteUniversities = api.searchUniversities()
                .map { it.toDomain() }
                .filter { it.name.isNotBlank() }
                .sortedBy { it.name }

            dao.replaceUniversities(remoteUniversities.map { it.toEntity() })
            remoteUniversities
        }.recoverCatching { throwable ->
            val cached = dao.getUniversities().map { it.toDomain() }
            cached.ifEmpty {
                throw throwable
            }
        }
    }
}
