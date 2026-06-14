package com.d3vly.core.data.repository

import com.d3vly.core.data.local.UniversityDao
import com.d3vly.core.data.mapper.toDomain
import com.d3vly.core.data.mapper.toEntity
import com.d3vly.core.data.remote.UniversityApi
import com.d3vly.core.domain.model.UniversityLoadResult
import com.d3vly.core.domain.model.UniversityLoadSource
import com.d3vly.core.domain.repository.UniversityRepository
import javax.inject.Inject

class UniversityRepositoryImpl @Inject constructor(
    private val api: UniversityApi,
    private val dao: UniversityDao,
) : UniversityRepository {
    override suspend fun getUniversities(): Result<UniversityLoadResult> {
        return runCatching {
            val remoteUniversities = api.searchUniversities()
                .map { it.toDomain() }
                .filter { it.name.isNotBlank() }
                .sortedBy { it.name }

            dao.replaceUniversities(remoteUniversities.map { it.toEntity() })
            UniversityLoadResult(
                universities = remoteUniversities,
                source = UniversityLoadSource.Remote,
            )
        }.recoverCatching { throwable ->
            val cached = dao.getUniversities().map { it.toDomain() }
            cached.ifEmpty {
                throw throwable
            }
            UniversityLoadResult(
                universities = cached,
                source = UniversityLoadSource.Cache,
            )
        }
    }
}
