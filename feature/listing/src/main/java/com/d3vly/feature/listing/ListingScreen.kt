package com.d3vly.feature.listing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.d3vly.core.designsystem.theme.D3vlyTestAppTheme
import com.d3vly.core.domain.model.University
import com.d3vly.feature.listing.components.ListingErrorState
import com.d3vly.feature.listing.components.ListingTopAppBar
import com.d3vly.feature.listing.components.UniversityList
import com.d3vly.feature.listing.components.previewUniversities
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ListingScreen(
    viewModel: ListingViewModel,
    onUniversitySelected: (University) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(ListingIntent.Load)
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is ListingEffect.OpenDetails -> onUniversitySelected(effect.university)
            }
        }
    }

    ListingContent(
        state = state,
        onRefresh = { viewModel.onIntent(ListingIntent.Refresh) },
        onUniversityClick = { viewModel.onIntent(ListingIntent.UniversityClicked(it)) },
        modifier = modifier,
    )
}

@Composable
private fun ListingContent(
    state: ListingState,
    onRefresh: () -> Unit,
    onUniversityClick: (University) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            ListingTopAppBar(
                isRefreshEnabled = !state.isLoading,
                onRefresh = onRefresh,
            )
        },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                state.isLoading && state.universities.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessageRes != null && state.universities.isEmpty() -> {
                    ListingErrorState(
                        message = stringResource(state.errorMessageRes),
                        onRetry = onRefresh,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                state.universities.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.listing_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    val statusMessageRes = state.errorMessageRes ?: state.warningMessageRes
                    UniversityList(
                        universities = state.universities,
                        isRefreshing = state.isLoading,
                        statusMessage = statusMessageRes?.let { stringResource(it) },
                        onUniversityClick = onUniversityClick,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListingContentPreview() {
    D3vlyTestAppTheme {
        ListingContent(
            state = ListingState(
                universities = previewUniversities,
            ),
            onRefresh = {},
            onUniversityClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ListingContentLoadingPreview() {
    D3vlyTestAppTheme {
        ListingContent(
            state = ListingState(isLoading = true),
            onRefresh = {},
            onUniversityClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ListingContentErrorPreview() {
    D3vlyTestAppTheme {
        ListingContent(
            state = ListingState(errorMessageRes = R.string.listing_error_unable_load),
            onRefresh = {},
            onUniversityClick = {},
        )
    }
}
