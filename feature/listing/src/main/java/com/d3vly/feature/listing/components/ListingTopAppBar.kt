package com.d3vly.feature.listing.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import com.d3vly.core.designsystem.theme.D3vlyTestAppTheme
import com.d3vly.feature.listing.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun ListingTopAppBar(
    isRefreshEnabled: Boolean,
    onRefresh: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.listing_title)) },
        actions = {
            Button(
                onClick = onRefresh,
                enabled = isRefreshEnabled,
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
}

@Preview
@Composable
private fun ListingTopAppBarPreview() {
    D3vlyTestAppTheme {
        ListingTopAppBar(
            isRefreshEnabled = true,
            onRefresh = {},
        )
    }
}
