package com.d3vly.testapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.d3vly.feature.details.navigation.DetailsNavigationContract
import com.d3vly.feature.listing.navigation.ListingNavigationContract
import com.d3vly.feature.listing.navigation.SelectedUniversityArgs
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

    private fun openDetails(university: SelectedUniversityArgs) {
        navHostFragment.navController.navigate(
            R.id.detailsFragment,
            university.toDetailsArgs().toBundle(),
        )
    }

    private fun observeListingResults() {
        navHostFragment.childFragmentManager.setFragmentResultListener(
            ListingNavigationContract.SELECTED_UNIVERSITY_REQUEST_KEY,
            this,
        ) { _, bundle ->
            ListingNavigationContract.selectedUniversityFrom(bundle)?.let { university ->
                openDetails(university)
            }
        }
    }

    private fun observeDetailsResults() {
        navHostFragment.childFragmentManager.setFragmentResultListener(
            DetailsNavigationContract.REFRESH_REQUEST_KEY,
            this,
        ) { _, bundle ->
            if (DetailsNavigationContract.isRefreshRequested(bundle)) {
                refreshListing()
            }
        }
    }

    private fun refreshListing() {
        navHostFragment.navController
            .getBackStackEntry(R.id.listingFragment)
            .savedStateHandle[ListingNavigationContract.REFRESH_REQUESTED_KEY] = true
    }

    private val navHostFragment: NavHostFragment
        get() = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
}
