package com.d3vly.feature.details.navigation

import android.os.Bundle
import androidx.fragment.app.FragmentManager

object DetailsNavigationContract {
    const val REFRESH_REQUEST_KEY = "com.d3vly.feature.details.REFRESH_REQUEST"

    private const val REFRESH_REQUESTED_KEY = "com.d3vly.feature.details.REFRESH_REQUESTED"

    fun publishRefreshRequested(fragmentManager: FragmentManager) {
        fragmentManager.setFragmentResult(
            REFRESH_REQUEST_KEY,
            Bundle().apply {
                putBoolean(REFRESH_REQUESTED_KEY, true)
            },
        )
    }

    fun isRefreshRequested(bundle: Bundle): Boolean {
        return bundle.getBoolean(REFRESH_REQUESTED_KEY)
    }
}
