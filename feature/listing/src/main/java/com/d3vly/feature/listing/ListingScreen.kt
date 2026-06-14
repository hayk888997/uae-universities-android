package com.d3vly.feature.listing

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.d3vly.core.designsystem.theme.D3vlyTestAppTheme
import com.d3vly.feature.listing.components.ListingErrorState
import com.d3vly.feature.listing.components.ListingTopAppBar
import com.d3vly.feature.listing.components.UniversityList
import com.d3vly.feature.listing.components.previewUniversities
import com.d3vly.feature.listing.navigation.SelectedUniversityArgs
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ListingScreen(
    viewModel: ListingViewModel,
    onUniversitySelected: (SelectedUniversityArgs) -> Unit,
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
    onUniversityClick: (UniversityUiModel) -> Unit,
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

                state.errorMessage != null && state.universities.isEmpty() -> {
                    ListingErrorState(
                        message = stringResource(state.errorMessage.stringRes),
                        onRetry = onRefresh,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                state.universities.isEmpty() -> {
                    ListingEmptyState()
                }

                else -> {
                    val statusMessage = state.errorMessage ?: state.warningMessage
                    UniversityList(
                        universities = state.universities,
                        isRefreshing = state.isLoading,
                        statusMessage = statusMessage?.let { stringResource(it.stringRes) },
                        onUniversityClick = onUniversityClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ListingEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.listing_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.widthIn(max = 520.dp),
        )
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
            state = ListingState(errorMessage = ListingMessage.UnableToLoad),
            onRefresh = {},
            onUniversityClick = {},
        )
    }
}

private val ListingMessage.stringRes: Int
    get() = when (this) {
        ListingMessage.UnableToLoad -> R.string.listing_error_unable_load
        ListingMessage.ShowingCachedData -> R.string.listing_warning_cached_data
        ListingMessage.CacheWriteFailed -> R.string.listing_warning_cache_write_failed
    }
