package com.d3vly.feature.listing

import com.d3vly.core.domain.model.University
import com.d3vly.core.domain.model.UniversitySearchTarget
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
        country = UniversitySearchTarget.COUNTRY,
        alphaTwoCode = UniversitySearchTarget.ALPHA_TWO_CODE,
        stateProvince = null,
        webPages = listOf("https://www.adu.ac.ae"),
        domains = listOf("adu.ac.ae"),
    )

    @Test
    fun `load intent exposes universities`() = runTest {
        val viewModel = ListingViewModel(
            GetUniversitiesUseCase(FakeUniversityRepository(Result.success(listOf(university)))),
        )

        viewModel.onIntent(ListingIntent.Load)
        advanceUntilIdle()

        assertEquals(listOf(university), viewModel.state.value.universities)
        assertNull(viewModel.state.value.errorMessageRes)
        assertTrue(!viewModel.state.value.isLoading)
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
        assertTrue(!viewModel.state.value.isLoading)
    }

    @Test
    fun `university click emits open details effect`() = runTest {
        val viewModel = ListingViewModel(
            GetUniversitiesUseCase(FakeUniversityRepository(Result.success(emptyList()))),
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
        val firstResult = CompletableDeferred<Result<List<University>>>()
        val refreshedUniversity = university.copy(name = "Zayed University")
        val repository = QueuedUniversityRepository(
            first = firstResult,
            second = Result.success(listOf(refreshedUniversity)),
        )
        val viewModel = ListingViewModel(GetUniversitiesUseCase(repository))

        viewModel.onIntent(ListingIntent.Load)
        viewModel.onIntent(ListingIntent.Refresh)

        assertEquals(1, repository.callCount)

        firstResult.complete(Result.success(listOf(university)))
        advanceUntilIdle()

        assertEquals(2, repository.callCount)
        assertEquals(listOf(refreshedUniversity), viewModel.state.value.universities)
    }

    private class FakeUniversityRepository(
        private val result: Result<List<University>>,
    ) : UniversityRepository {
        override suspend fun getUniversities(): Result<List<University>> = result
    }

    private class QueuedUniversityRepository(
        private val first: CompletableDeferred<Result<List<University>>>,
        private val second: Result<List<University>>,
    ) : UniversityRepository {
        var callCount = 0
            private set

        override suspend fun getUniversities(): Result<List<University>> {
            callCount += 1
            return if (callCount == 1) first.await() else second
        }
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
