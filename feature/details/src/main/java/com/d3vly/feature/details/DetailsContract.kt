package com.d3vly.feature.details

import androidx.annotation.StringRes
import com.d3vly.feature.details.navigation.UniversityDetailsArgs

sealed interface DetailsIntent {
    data class Load(val args: UniversityDetailsArgs?) : DetailsIntent
    data object RefreshClicked : DetailsIntent
}

data class DetailsState(
    val name: String = "",
    val country: String = "",
    val alphaTwoCode: String = "",
    val stateProvince: String? = null,
    val webPages: List<String> = emptyList(),
    val domains: List<String> = emptyList(),
    @StringRes val errorMessageRes: Int? = null,
)

sealed interface DetailsEffect {
    data object CloseAndRefreshListing : DetailsEffect
}
