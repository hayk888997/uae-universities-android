package com.d3vly.feature.listing.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.d3vly.core.designsystem.theme.D3vlyTestAppTheme
import com.d3vly.feature.listing.R

@Composable
internal fun ListingErrorState(
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

@Preview(showBackground = true)
@Composable
private fun ListingErrorStatePreview() {
    D3vlyTestAppTheme {
        ListingErrorState(
            message = "Unable to load universities",
            onRetry = {},
        )
    }
}
