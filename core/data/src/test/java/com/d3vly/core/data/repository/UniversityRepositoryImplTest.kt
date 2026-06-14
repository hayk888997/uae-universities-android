package com.d3vly.core.data.repository

import com.d3vly.core.data.local.UniversityDao
import com.d3vly.core.data.local.UniversityEntity
import com.d3vly.core.data.remote.UniversityApi
import com.d3vly.core.data.remote.UniversityDto
import com.d3vly.core.data.remote.UniversitySearchConfig
import com.d3vly.core.domain.model.UniversityLoadSource
import com.d3vly.core.domain.model.UniversitiesResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class UniversityRepositoryImplTest {
    private val remoteA = UniversityDto(
        name = "Abu Dhabi University",
        country = UniversitySearchConfig.COUNTRY,
        alphaTwoCode = ALPHA_TWO_CODE,
        stateProvince = null,
        webPages = listOf("https://www.adu.ac.ae"),
        domains = listOf("adu.ac.ae"),
    )
    private val remoteB = UniversityDto(
        name = "Zayed University",
        country = UniversitySearchConfig.COUNTRY,
        alphaTwoCode = ALPHA_TWO_CODE,
        stateProvince = null,
        webPages = listOf("https://www.zu.ac.ae"),
        domains = listOf("zu.ac.ae"),
    )

    @Test
    fun `getUniversities fetches remote data and replaces cache`() = runTest {
        val api = FakeUniversityApi(Result.success(listOf(remoteB, remoteA, remoteA.copy(name = ""))))
        val dao = FakeUniversityDao()
        val repository = UniversityRepositoryImpl(api, dao)

        val result = repository.getUniversities()

        assertTrue(result is UniversitiesResult.Success)
        result as UniversitiesResult.Success
        assertEquals(UniversityLoadSource.Remote, result.result.source)
        assertEquals(listOf("Abu Dhabi University", "Zayed University"), result.result.universities.map { it.name })
        assertEquals(listOf("Abu Dhabi University", "Zayed University"), dao.cached.map { it.name })
        assertEquals(1, api.callCount)
    }

    @Test
    fun `getUniversities returns remote data when cache write fails`() = runTest {
        val cacheWriteFailure = IllegalStateException("Cache write failed")
        val api = FakeUniversityApi(Result.success(listOf(remoteA)))
        val dao = FakeUniversityDao(insertFailure = cacheWriteFailure)
        val repository = UniversityRepositoryImpl(api, dao)

        val result = repository.getUniversities()

        assertTrue(result is UniversitiesResult.Success)
        result as UniversitiesResult.Success
        assertEquals(UniversityLoadSource.Remote, result.result.source)
        assertTrue(result.result.cacheWriteFailed)
        assertEquals("Abu Dhabi University", result.result.universities.single().name)
    }

    @Test
    fun `getUniversities falls back to cached data when remote fails`() = runTest {
        val failure = IllegalStateException("Network error")
        val api = FakeUniversityApi(Result.failure(failure))
        val dao = FakeUniversityDao(
            initialCache = listOf(
                UniversityEntity(
                    id = "cached|ae",
                    name = "Cached University",
                    country = UniversitySearchConfig.COUNTRY,
                    alphaTwoCode = ALPHA_TWO_CODE,
                    stateProvince = null,
                    webPages = listOf("https://cached.example"),
                    domains = listOf("cached.example"),
                ),
            ),
        )
        val repository = UniversityRepositoryImpl(api, dao)

        val result = repository.getUniversities()

        assertTrue(result is UniversitiesResult.Success)
        result as UniversitiesResult.Success
        assertEquals(UniversityLoadSource.Cache, result.result.source)
        assertEquals("Cached University", result.result.universities.single().name)
        assertEquals(1, api.callCount)
    }

    @Test
    fun `getUniversities returns failure when remote fails and cache is empty`() = runTest {
        val failure = IllegalStateException("Network error")
        val api = FakeUniversityApi(Result.failure(failure))
        val dao = FakeUniversityDao()
        val repository = UniversityRepositoryImpl(api, dao)

        val result = repository.getUniversities()

        assertTrue(result is UniversitiesResult.Error)
        result as UniversitiesResult.Error
        assertSame(failure, result.cause)
        assertTrue(result.cause.suppressed.isEmpty())
    }

    @Test
    fun `getUniversities preserves remote failure when cache read fails`() = runTest {
        val remoteFailure = IllegalStateException("Network error")
        val cacheReadFailure = IllegalStateException("Cache read failed")
        val api = FakeUniversityApi(Result.failure(remoteFailure))
        val dao = FakeUniversityDao(getFailure = cacheReadFailure)
        val repository = UniversityRepositoryImpl(api, dao)

        val result = repository.getUniversities()

        assertTrue(result is UniversitiesResult.Error)
        result as UniversitiesResult.Error
        assertSame(remoteFailure, result.cause)
        assertEquals(listOf(cacheReadFailure), result.cause.suppressed.toList())
    }

    private class FakeUniversityApi(
        private val result: Result<List<UniversityDto>>,
    ) : UniversityApi {
        var callCount = 0
            private set

        override suspend fun searchUniversities(country: String): List<UniversityDto> {
            callCount += 1
            assertEquals(UniversitySearchConfig.COUNTRY, country)
            return result.getOrThrow()
        }
    }

    private class FakeUniversityDao(
        initialCache: List<UniversityEntity> = emptyList(),
        private val getFailure: Throwable? = null,
        private val insertFailure: Throwable? = null,
    ) : UniversityDao {
        var cached: List<UniversityEntity> = initialCache
            private set

        override suspend fun getUniversities(): List<UniversityEntity> {
            getFailure?.let { throw it }
            return cached
        }

        override suspend fun insertUniversities(universities: List<UniversityEntity>) {
            insertFailure?.let { throw it }
            cached = cached + universities
        }

        override suspend fun clearUniversities() {
            cached = emptyList()
        }
    }

    private companion object {
        const val ALPHA_TWO_CODE = "AE"
    }
}
