package com.d3vly.feature.details

import com.d3vly.feature.details.navigation.UniversityDetailsArgs
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
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val args = UniversityDetailsArgs(
        name = "Abu Dhabi University",
        country = COUNTRY,
        alphaTwoCode = ALPHA_TWO_CODE,
        stateProvince = null,
        webPages = listOf("https://www.adu.ac.ae"),
        domains = listOf("adu.ac.ae"),
    )

    @Test
    fun `load intent exposes selected university details`() {
        val viewModel = DetailsViewModel()

        viewModel.onIntent(DetailsIntent.Load(args))

        assertEquals(
            DetailsState(
                name = "Abu Dhabi University",
                country = COUNTRY,
                alphaTwoCode = ALPHA_TWO_CODE,
                stateProvince = null,
                webPages = listOf("https://www.adu.ac.ae"),
                domains = listOf("adu.ac.ae"),
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun `load intent exposes error when args are missing`() {
        val viewModel = DetailsViewModel()

        viewModel.onIntent(DetailsIntent.Load(null))

        assertEquals(R.string.details_error_missing_university, viewModel.state.value.errorMessageRes)
    }

    @Test
    fun `refresh click emits close and refresh listing effect`() = runTest {
        val viewModel = DetailsViewModel()
        val effects = mutableListOf<DetailsEffect>()
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.effects.take(1).toList(effects)
        }

        viewModel.onIntent(DetailsIntent.RefreshClicked)
        advanceUntilIdle()

        assertEquals(listOf(DetailsEffect.CloseAndRefreshListing), effects)
        collectJob.cancel()
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
