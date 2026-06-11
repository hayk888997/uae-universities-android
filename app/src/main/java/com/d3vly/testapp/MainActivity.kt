package com.d3vly.testapp

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.d3vly.core.domain.model.University
import com.d3vly.feature.details.DetailsActivity
import com.d3vly.feature.details.UniversityDetailsArgs
import com.d3vly.feature.listing.ListingScreen
import com.d3vly.testapp.ui.theme.D3vlyTestAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val detailsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (DetailsActivity.isRefreshRequested(result.data)) {
            refreshListing()
        }
    }

    private var refreshListing: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var refreshSignal by remember { mutableIntStateOf(0) }
            refreshListing = { refreshSignal += 1 }

            D3vlyTestAppTheme {
                ListingScreen(
                    refreshSignal = refreshSignal,
                    onUniversitySelected = { university ->
                        openDetails(university)
                    },
                )
            }
        }
    }

    private fun openDetails(university: University) {
        detailsLauncher.launch(
            DetailsActivity.createIntent(
                context = this,
                args = UniversityDetailsArgs(
                    name = university.name,
                    country = university.country,
                    alphaTwoCode = university.alphaTwoCode,
                    stateProvince = university.stateProvince,
                    webPages = university.webPages,
                    domains = university.domains,
                ),
            ),
        )
    }
}
