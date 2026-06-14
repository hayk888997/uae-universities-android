package com.d3vly.core.navigation

import androidx.lifecycle.SavedStateHandle

object ListingRefreshResultContract {
    private const val REFRESH_REQUESTED_KEY = "com.d3vly.core.navigation.REFRESH_LISTING_REQUESTED"

    fun requestRefresh(savedStateHandle: SavedStateHandle?) {
        savedStateHandle?.set(REFRESH_REQUESTED_KEY, true)
    }

    fun refreshRequests(savedStateHandle: SavedStateHandle) =
        savedStateHandle.getLiveData<Boolean>(REFRESH_REQUESTED_KEY)

    fun consumeRefreshRequest(savedStateHandle: SavedStateHandle) {
        savedStateHandle.remove<Boolean>(REFRESH_REQUESTED_KEY)
    }
}
