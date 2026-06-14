package com.d3vly.feature.listing.navigation

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentManager

object ListingNavigationContract {
    const val SELECTED_UNIVERSITY_REQUEST_KEY = "com.d3vly.feature.listing.SELECTED_UNIVERSITY_REQUEST"

    private const val SELECTED_UNIVERSITY_ARGS_KEY = "com.d3vly.feature.listing.SELECTED_UNIVERSITY_ARGS"

    fun publishSelectedUniversity(
        fragmentManager: FragmentManager,
        university: SelectedUniversityArgs,
    ) {
        fragmentManager.setFragmentResult(
            SELECTED_UNIVERSITY_REQUEST_KEY,
            Bundle().apply {
                putParcelable(SELECTED_UNIVERSITY_ARGS_KEY, university)
            },
        )
    }

    fun selectedUniversityFrom(bundle: Bundle): SelectedUniversityArgs? {
        return bundle.getSelectedUniversityArgs()
    }

    @Suppress("DEPRECATION")
    private fun Bundle.getSelectedUniversityArgs(): SelectedUniversityArgs? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelable(SELECTED_UNIVERSITY_ARGS_KEY, SelectedUniversityArgs::class.java)
        } else {
            getParcelable(SELECTED_UNIVERSITY_ARGS_KEY)
        }
    }
}
