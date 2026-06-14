package com.d3vly.feature.listing.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.d3vly.core.designsystem.theme.D3vlyTestAppTheme
import com.d3vly.feature.listing.UniversityUiModel
import com.d3vly.feature.listing.R

@Composable
internal fun UniversityRow(
    university: UniversityUiModel,
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
                text = webPage.ifBlank { stringResource(R.string.listing_website_unavailable) },
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

@Preview(showBackground = true)
@Composable
private fun UniversityRowPreview() {
    D3vlyTestAppTheme {
        UniversityRow(
            university = previewUniversity,
            onClick = {},
        )
    }
}
