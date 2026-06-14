package com.d3vly.testapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
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

    private val navHostFragment: NavHostFragment
        get() = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
}
