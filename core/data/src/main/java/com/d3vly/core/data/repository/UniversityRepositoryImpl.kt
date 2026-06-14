package com.d3vly.core.data.repository

import com.d3vly.core.data.local.UniversityDao
import com.d3vly.core.data.mapper.toDomain
import com.d3vly.core.data.mapper.toEntity
import com.d3vly.core.data.remote.UniversityApi
import com.d3vly.core.domain.model.University
import com.d3vly.core.domain.model.UniversityLoadResult
import com.d3vly.core.domain.model.UniversityLoadSource
import com.d3vly.core.domain.model.UniversitiesResult
import com.d3vly.core.domain.repository.UniversityRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class UniversityRepositoryImpl @Inject constructor(
    private val api: UniversityApi,
    private val dao: UniversityDao,
) : UniversityRepository {
    override suspend fun getUniversities(): UniversitiesResult {
        val remoteResult = fetchRemoteUniversities()

        return remoteResult.fold(
            onSuccess = { remoteUniversities ->
                val cacheWriteFailed = dao.cacheWriteFailed(remoteUniversities)
                UniversitiesResult.Success(
                    UniversityLoadResult(
                        universities = remoteUniversities,
                        source = UniversityLoadSource.Remote,
                        cacheWriteFailed = cacheWriteFailed,
                    ),
                )
            },
            onFailure = { remoteError ->
                loadCachedUniversities(remoteError)
            },
        )
    }

    private suspend fun fetchRemoteUniversities(): Result<List<University>> {
        return try {
            Result.success(
                api.searchUniversities()
                    .map { it.toDomain() }
                    .filter { it.name.isNotBlank() }
                    .sortedBy { it.name },
            )
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            Result.failure(error)
        }
    }

    private suspend fun UniversityDao.cacheWriteFailed(
        remoteUniversities: List<University>,
    ): Boolean {
        return try {
            replaceUniversities(remoteUniversities.map { it.toEntity() })
            false
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            true
        }
    }

    private suspend fun loadCachedUniversities(remoteError: Throwable): UniversitiesResult {
        return try {
            val cached = dao.getUniversities().map { it.toDomain() }
            if (cached.isEmpty()) {
                UniversitiesResult.Error(remoteError)
            } else {
                UniversitiesResult.Success(
                    UniversityLoadResult(
                        universities = cached,
                        source = UniversityLoadSource.Cache,
                    ),
                )
            }
        } catch (cacheError: Throwable) {
            if (cacheError is CancellationException) throw cacheError
            UniversitiesResult.Error(
                remoteError.apply {
                    addSuppressed(cacheError)
                },
            )
        }
    }
}
