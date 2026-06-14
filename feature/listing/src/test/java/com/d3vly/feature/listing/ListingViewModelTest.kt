package com.d3vly.feature.listing

import com.d3vly.core.domain.model.University
import com.d3vly.core.domain.model.UniversityLoadResult
import com.d3vly.core.domain.model.UniversityLoadSource
import com.d3vly.core.domain.repository.UniversityRepository
import com.d3vly.core.domain.usecase.GetUniversitiesUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

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

        assertEquals(listOf(university), viewModel.state.value.universities)
        assertNull(viewModel.state.value.errorMessageRes)
        assertNull(viewModel.state.value.warningMessageRes)
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
        assertEquals(listOf(university), viewModel.state.value.universities)
    }

    @Test
    fun `load intent exposes error when use case fails`() = runTest {
        val viewModel = ListingViewModel(
            GetUniversitiesUseCase(FakeUniversityRepository(Result.failure(IllegalStateException("Network error")))),
        )

        viewModel.onIntent(ListingIntent.Load)
        advanceUntilIdle()

        assertEquals(emptyList<University>(), viewModel.state.value.universities)
        assertEquals(R.string.listing_error_unable_load, viewModel.state.value.errorMessageRes)
        assertNull(viewModel.state.value.warningMessageRes)
        assertTrue(!viewModel.state.value.isLoading)
    }

    @Test
    fun `load intent exposes cached data warning when repository falls back to cache`() = runTest {
        val viewModel = ListingViewModel(
            GetUniversitiesUseCase(FakeUniversityRepository(loadSuccess(listOf(university), UniversityLoadSource.Cache))),
        )

        viewModel.onIntent(ListingIntent.Load)
        advanceUntilIdle()

        assertEquals(listOf(university), viewModel.state.value.universities)
        assertNull(viewModel.state.value.errorMessageRes)
        assertEquals(R.string.listing_warning_cached_data, viewModel.state.value.warningMessageRes)
    }

    @Test
    fun `refresh failure keeps existing list and exposes error`() = runTest {
        val repository = QueuedUniversityRepository(
            first = CompletableDeferred(loadSuccess(listOf(university))),
            second = Result.failure(IllegalStateException("Network error")),
        )
        val viewModel = ListingViewModel(GetUniversitiesUseCase(repository))

        viewModel.onIntent(ListingIntent.Load)
        advanceUntilIdle()
        viewModel.onIntent(ListingIntent.Refresh)
        advanceUntilIdle()

        assertEquals(listOf(university), viewModel.state.value.universities)
        assertEquals(R.string.listing_error_unable_load, viewModel.state.value.errorMessageRes)
        assertNull(viewModel.state.value.warningMessageRes)
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

        viewModel.onIntent(ListingIntent.UniversityClicked(university))
        advanceUntilIdle()

        assertEquals(listOf(ListingEffect.OpenDetails(university)), effects)
        collectJob.cancel()
    }

    @Test
    fun `refresh requested while loading runs after current load completes`() = runTest {
        val firstResult = CompletableDeferred<Result<UniversityLoadResult>>()
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
        assertEquals(listOf(refreshedUniversity), viewModel.state.value.universities)
    }

    private class FakeUniversityRepository(
        private val result: Result<UniversityLoadResult>,
    ) : UniversityRepository {
        var callCount = 0
            private set

        override suspend fun getUniversities(): Result<UniversityLoadResult> {
            callCount += 1
            return result
        }
    }

    private class QueuedUniversityRepository(
        private val first: CompletableDeferred<Result<UniversityLoadResult>>,
        private val second: Result<UniversityLoadResult>,
    ) : UniversityRepository {
        var callCount = 0
            private set

        override suspend fun getUniversities(): Result<UniversityLoadResult> {
            callCount += 1
            return if (callCount == 1) first.await() else second
        }
    }

    private fun loadSuccess(
        universities: List<University>,
        source: UniversityLoadSource = UniversityLoadSource.Remote,
    ): Result<UniversityLoadResult> {
        return Result.success(
            UniversityLoadResult(
                universities = universities,
                source = source,
            ),
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

private const val COUNTRY = "United Arab Emirates"
private const val ALPHA_TWO_CODE = "AE"
