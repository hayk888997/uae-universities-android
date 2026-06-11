package com.d3vly.core.data.repository

import com.d3vly.core.data.local.UniversityDao
import com.d3vly.core.data.local.UniversityEntity
import com.d3vly.core.data.remote.UniversityApi
import com.d3vly.core.data.remote.UniversityDto
import com.d3vly.core.domain.model.UniversitySearchTarget
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class UniversityRepositoryImplTest {
    private val remoteA = UniversityDto(
        name = "Abu Dhabi University",
        country = UniversitySearchTarget.COUNTRY,
        alphaTwoCode = UniversitySearchTarget.ALPHA_TWO_CODE,
        stateProvince = null,
        webPages = listOf("https://www.adu.ac.ae"),
        domains = listOf("adu.ac.ae"),
    )
    private val remoteB = UniversityDto(
        name = "Zayed University",
        country = UniversitySearchTarget.COUNTRY,
        alphaTwoCode = UniversitySearchTarget.ALPHA_TWO_CODE,
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

        assertTrue(result.isSuccess)
        assertEquals(listOf("Abu Dhabi University", "Zayed University"), result.getOrThrow().map { it.name })
        assertEquals(listOf("Abu Dhabi University", "Zayed University"), dao.cached.map { it.name })
        assertEquals(1, api.callCount)
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
                    country = UniversitySearchTarget.COUNTRY,
                    alphaTwoCode = UniversitySearchTarget.ALPHA_TWO_CODE,
                    stateProvince = null,
                    webPages = listOf("https://cached.example"),
                    domains = listOf("cached.example"),
                ),
            ),
        )
        val repository = UniversityRepositoryImpl(api, dao)

        val result = repository.getUniversities()

        assertTrue(result.isSuccess)
        assertEquals("Cached University", result.getOrThrow().single().name)
        assertEquals(1, api.callCount)
    }

    @Test
    fun `getUniversities returns failure when remote fails and cache is empty`() = runTest {
        val failure = IllegalStateException("Network error")
        val api = FakeUniversityApi(Result.failure(failure))
        val dao = FakeUniversityDao()
        val repository = UniversityRepositoryImpl(api, dao)

        val result = repository.getUniversities()

        assertTrue(result.isFailure)
        assertSame(failure, result.exceptionOrNull())
    }

    private class FakeUniversityApi(
        private val result: Result<List<UniversityDto>>,
    ) : UniversityApi {
        var callCount = 0
            private set

        override suspend fun searchUniversities(country: String): List<UniversityDto> {
            callCount += 1
            assertEquals(UniversitySearchTarget.COUNTRY, country)
            return result.getOrThrow()
        }
    }

    private class FakeUniversityDao(
        initialCache: List<UniversityEntity> = emptyList(),
    ) : UniversityDao {
        var cached: List<UniversityEntity> = initialCache
            private set

        override suspend fun getUniversities(): List<UniversityEntity> = cached

        override suspend fun insertUniversities(universities: List<UniversityEntity>) {
            cached = cached + universities
        }

        override suspend fun clearUniversities() {
            cached = emptyList()
        }
    }
}
