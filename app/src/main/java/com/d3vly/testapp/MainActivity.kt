package com.d3vly.testapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.d3vly.core.domain.model.University
import com.d3vly.feature.details.DetailsFragment
import com.d3vly.feature.details.UniversityDetailsArgs
import com.d3vly.feature.listing.ListingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity(), ListingFragment.Listener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        observeDetailsResults()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(android.R.id.content, ListingFragment.newInstance(), ListingFragment.TAG)
                .commit()
        }
    }

    override fun onUniversitySelected(university: University) {
        val args = UniversityDetailsArgs(
            name = university.name,
            country = university.country,
            alphaTwoCode = university.alphaTwoCode,
            stateProvince = university.stateProvince,
            webPages = university.webPages,
            domains = university.domains,
        )

        supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .add(android.R.id.content, DetailsFragment.newInstance(args), DetailsFragment.TAG)
            .addToBackStack(DetailsFragment.TAG)
            .commit()
    }

    private fun observeDetailsResults() {
        supportFragmentManager.setFragmentResultListener(
            DetailsFragment.REQUEST_KEY,
            this,
        ) { _, bundle ->
            if (bundle.getBoolean(DetailsFragment.RESULT_REFRESH_REQUESTED)) {
                refreshListing()
            }
        }
    }

    private fun refreshListing() {
        val listingFragment = supportFragmentManager.findFragmentByTag(ListingFragment.TAG) as? ListingFragment
        listingFragment?.refresh()
    }
}
