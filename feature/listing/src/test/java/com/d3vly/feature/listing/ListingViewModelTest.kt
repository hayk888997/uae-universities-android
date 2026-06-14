package com.d3vly.feature.listing

import com.d3vly.core.domain.model.University
import com.d3vly.core.domain.model.UniversityLoadResult
import com.d3vly.core.domain.model.UniversityLoadSource
import com.d3vly.core.domain.model.UniversitiesResult
import com.d3vly.core.domain.repository.UniversityRepository
import com.d3vly.core.domain.usecase.GetUniversitiesUseCase
import com.d3vly.core.testing.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val university = University(
        name = "Abu Dhabi University",
        country = COUNTRY,
        alphaTwoCode = ALPHA_TWO_CODE,
        stateProvince = null,
        webPages = listOf("https://www.adu.ac.ae"),
        domains = listOf("adu.ac.ae"),
    )

    @Test
    fun `load intent exposes universities`() = runTest {
        val viewModel = ListingViewModel(
            GetUniversitiesUseCase(FakeUniversityRepository(loadSuccess(listOf(university)))),
        )

        viewModel.onIntent(ListingIntent.Load)
        advanceUntilIdle()

        assertEquals(listOf(university.toUiModel()), viewModel.state.value.universities)
        assertNull(viewModel.state.value.errorMessage)
        assertNull(viewModel.state.value.warningMessage)
        assertTrue(!viewModel.state.value.isLoading)
    }

    @Test
    fun `load intent is ignored after initial load`() = runTest {
        val repository = FakeUniversityRepository(loadSuccess(listOf(university)))
        val viewModel = ListingViewModel(GetUniversitiesUseCase(repository))

        viewModel.onIntent(ListingIntent.Load)
        advanceUntilIdle()
        viewModel.onIntent(ListingIntent.Load)
        advanceUntilIdle()

        assertEquals(1, repository.callCount)
        assertEquals(listOf(university.toUiModel()), viewModel.state.value.universities)
    }

    @Test
    fun `load intent exposes error when use case fails`() = runTest {
        val viewModel = ListingViewModel(
            GetUniversitiesUseCase(FakeUniversityRepository(UniversitiesResult.Error(IllegalStateException("Network error")))),
        )

        viewModel.onIntent(ListingIntent.Load)
        advanceUntilIdle()

        assertEquals(emptyList<UniversityUiModel>(), viewModel.state.value.universities)
        assertEquals(ListingMessage.UnableToLoad, viewModel.state.value.errorMessage)
        assertNull(viewModel.state.value.warningMessage)
        assertTrue(!viewModel.state.value.isLoading)
    }

    @Test
    fun `load intent exposes cached data warning when repository falls back to cache`() = runTest {
        val viewModel = ListingViewModel(
            GetUniversitiesUseCase(FakeUniversityRepository(loadSuccess(listOf(university), UniversityLoadSource.Cache))),
        )

        viewModel.onIntent(ListingIntent.Load)
        advanceUntilIdle()

        assertEquals(listOf(university.toUiModel()), viewModel.state.value.universities)
        assertNull(viewModel.state.value.errorMessage)
        assertEquals(ListingMessage.ShowingCachedData, viewModel.state.value.warningMessage)
    }

    @Test
    fun `load intent exposes cache write warning when remote data could not be saved`() = runTest {
        val viewModel = ListingViewModel(
            GetUniversitiesUseCase(
                FakeUniversityRepository(
                    loadSuccess(
                        universities = listOf(university),
                        cacheWriteFailed = true,
                    ),
                ),
            ),
        )

        viewModel.onIntent(ListingIntent.Load)
        advanceUntilIdle()

        assertEquals(listOf(university.toUiModel()), viewModel.state.value.universities)
        assertNull(viewModel.state.value.errorMessage)
        assertEquals(ListingMessage.CacheWriteFailed, viewModel.state.value.warningMessage)
    }

    @Test
    fun `refresh failure keeps existing list and exposes error`() = runTest {
        val repository = QueuedUniversityRepository(
            first = CompletableDeferred(loadSuccess(listOf(university))),
            second = UniversitiesResult.Error(IllegalStateException("Network error")),
        )
        val viewModel = ListingViewModel(GetUniversitiesUseCase(repository))

        viewModel.onIntent(ListingIntent.Load)
        advanceUntilIdle()
        viewModel.onIntent(ListingIntent.Refresh)
        advanceUntilIdle()

        assertEquals(listOf(university.toUiModel()), viewModel.state.value.universities)
        assertEquals(ListingMessage.UnableToLoad, viewModel.state.value.errorMessage)
        assertNull(viewModel.state.value.warningMessage)
    }

    @Test
    fun `university click emits open details effect`() = runTest {
        val viewModel = ListingViewModel(
            GetUniversitiesUseCase(FakeUniversityRepository(loadSuccess(emptyList()))),
        )
        val effects = mutableListOf<ListingEffect>()
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.effects.take(1).toList(effects)
        }

        viewModel.onIntent(ListingIntent.UniversityClicked(university.toUiModel()))
        advanceUntilIdle()

        assertEquals(listOf(ListingEffect.OpenDetails(university.toUiModel().toSelectedUniversityArgs())), effects)
        collectJob.cancel()
    }

    @Test
    fun `refresh requested while loading runs after current load completes`() = runTest {
        val firstResult = CompletableDeferred<UniversitiesResult>()
        val refreshedUniversity = university.copy(name = "Zayed University")
        val repository = QueuedUniversityRepository(
            first = firstResult,
            second = loadSuccess(listOf(refreshedUniversity)),
        )
        val viewModel = ListingViewModel(GetUniversitiesUseCase(repository))

        viewModel.onIntent(ListingIntent.Load)
        viewModel.onIntent(ListingIntent.Refresh)

        assertEquals(1, repository.callCount)

        firstResult.complete(loadSuccess(listOf(university)))
        advanceUntilIdle()

        assertEquals(2, repository.callCount)
        assertEquals(listOf(refreshedUniversity.toUiModel()), viewModel.state.value.universities)
    }

    private class FakeUniversityRepository(
        private val result: UniversitiesResult,
    ) : UniversityRepository {
        var callCount = 0
            private set

        override suspend fun getUniversities(): UniversitiesResult {
            callCount += 1
            return result
        }
    }

    private class QueuedUniversityRepository(
        private val first: CompletableDeferred<UniversitiesResult>,
        private val second: UniversitiesResult,
    ) : UniversityRepository {
        var callCount = 0
            private set

        override suspend fun getUniversities(): UniversitiesResult {
            callCount += 1
            return if (callCount == 1) first.await() else second
        }
    }

    private fun loadSuccess(
        universities: List<University>,
        source: UniversityLoadSource = UniversityLoadSource.Remote,
        cacheWriteFailed: Boolean = false,
    ): UniversitiesResult {
        return UniversitiesResult.Success(
            UniversityLoadResult(
                universities = universities,
                source = source,
                cacheWriteFailed = cacheWriteFailed,
            ),
        )
    }
}

private const val COUNTRY = "United Arab Emirates"
private const val ALPHA_TWO_CODE = "AE"
