package com.d3vly.feature.listing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.d3vly.core.domain.model.University
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ListingScreen(
    refreshSignal: Int,
    onUniversitySelected: (University) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(ListingIntent.Load)
    }

    LaunchedEffect(refreshSignal) {
        if (refreshSignal > 0) {
            viewModel.onIntent(ListingIntent.Refresh)
        }
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
@OptIn(ExperimentalMaterial3Api::class)
private fun ListingContent(
    state: ListingState,
    onRefresh: () -> Unit,
    onUniversityClick: (University) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.listing_title)) },
                actions = {
                    Button(
                        onClick = onRefresh,
                        enabled = !state.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                    ) {
                        Text(text = stringResource(R.string.listing_refresh))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
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
                    ErrorState(
                        message = stringResource(state.errorMessageRes),
                        onRetry = onRefresh,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    UniversityList(
                        universities = state.universities,
                        isRefreshing = state.isLoading,
                        onUniversityClick = onUniversityClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun UniversityList(
    universities: List<University>,
    isRefreshing: Boolean,
    onUniversityClick: (University) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (isRefreshing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            itemsIndexed(
                items = universities,
                key = { index, university -> "${university.name}-${university.country}-$index" },
            ) { _, university ->
                UniversityRow(
                    university = university,
                    onClick = { onUniversityClick(university) },
                )
            }
        }
    }
}

@Composable
private fun UniversityRow(
    university: University,
    onClick: () -> Unit,
) {
    val webPage = university.webPages.firstOrNull().orEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = university.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                softWrap = true,
            )
            Text(
                text = webPage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = university.country.ifBlank { stringResource(R.string.listing_unknown_country) },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.listing_try_again))
        }
    }
}
