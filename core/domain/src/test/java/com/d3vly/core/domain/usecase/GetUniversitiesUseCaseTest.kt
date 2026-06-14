package com.d3vly.core.domain.usecase

import com.d3vly.core.domain.model.University
import com.d3vly.core.domain.model.UniversityLoadResult
import com.d3vly.core.domain.model.UniversityLoadSource
import com.d3vly.core.domain.repository.UniversityRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetUniversitiesUseCaseTest {
    @Test
    fun `invoke delegates to repository`() = runTest {
        val universities = listOf(
            University(
                name = "Abu Dhabi University",
                country = COUNTRY,
                alphaTwoCode = ALPHA_TWO_CODE,
                stateProvince = null,
                webPages = listOf("https://www.adu.ac.ae"),
                domains = listOf("adu.ac.ae"),
            ),
        )
        val loadResult = UniversityLoadResult(
            universities = universities,
            source = UniversityLoadSource.Remote,
        )
        val repository = FakeUniversityRepository(Result.success(loadResult))
        val useCase = GetUniversitiesUseCase(repository)

        val result = useCase()

        assertTrue(repository.wasCalled)
        assertEquals(loadResult, result.getOrThrow())
    }

    private class FakeUniversityRepository(
        private val result: Result<UniversityLoadResult>,
    ) : UniversityRepository {
        var wasCalled = false
            private set

        override suspend fun getUniversities(): Result<UniversityLoadResult> {
            wasCalled = true
            return result
        }
    }

    private companion object {
        const val COUNTRY = "United Arab Emirates"
        const val ALPHA_TWO_CODE = "AE"
    }
}
