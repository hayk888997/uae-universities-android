package com.d3vly.feature.listing.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.d3vly.core.designsystem.theme.D3vlyTestAppTheme
import com.d3vly.core.domain.model.University

@Composable
internal fun UniversityList(
    universities: List<University>,
    isRefreshing: Boolean,
    statusMessage: String?,
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

        if (statusMessage != null) {
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
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

@Preview(showBackground = true)
@Composable
private fun UniversityListPreview() {
    D3vlyTestAppTheme {
        UniversityList(
            universities = previewUniversities,
            isRefreshing = true,
            statusMessage = "Showing cached data",
            onUniversityClick = {},
        )
    }
}
