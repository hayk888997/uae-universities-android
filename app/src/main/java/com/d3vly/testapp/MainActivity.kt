package com.d3vly.testapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.d3vly.core.domain.model.University
import com.d3vly.feature.details.DetailsFragment
import com.d3vly.feature.details.navigation.toUniversityDetailsArgs
import com.d3vly.feature.listing.ListingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        observeListingResults()
        observeDetailsResults()
    }

    private fun openDetails(university: University) {
        navHostFragment.navController.navigate(
            R.id.detailsFragment,
            university.toUniversityDetailsArgs().toBundle(),
        )
    }

    private fun observeListingResults() {
        navHostFragment.childFragmentManager.setFragmentResultListener(
            ListingFragment.REQUEST_KEY,
            this,
        ) { _, bundle ->
            ListingFragment.getSelectedUniversity(bundle)?.let { university ->
                openDetails(university)
            }
        }
    }

    private fun observeDetailsResults() {
        navHostFragment.childFragmentManager.setFragmentResultListener(
            DetailsFragment.REQUEST_KEY,
            this,
        ) { _, bundle ->
            if (bundle.getBoolean(DetailsFragment.RESULT_REFRESH_REQUESTED)) {
                refreshListing()
            }
        }
    }

    private fun refreshListing() {
        navHostFragment.navController
            .getBackStackEntry(R.id.listingFragment)
            .savedStateHandle[ListingFragment.REFRESH_REQUESTED_KEY] = true
    }

    private val navHostFragment: NavHostFragment
        get() = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
}
