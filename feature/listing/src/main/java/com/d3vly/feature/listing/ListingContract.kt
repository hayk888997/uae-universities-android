package com.d3vly.feature.listing

import com.d3vly.feature.listing.navigation.SelectedUniversityArgs

sealed interface ListingIntent {
    data object Load : ListingIntent
    data object Refresh : ListingIntent
    data class UniversityClicked(val university: UniversityUiModel) : ListingIntent
}

data class ListingState(
    val isLoading: Boolean = false,
    val universities: List<UniversityUiModel> = emptyList(),
    val errorMessage: ListingMessage? = null,
    val warningMessage: ListingMessage? = null,
)

data class UniversityUiModel(
    val name: String,
    val country: String,
    val alphaTwoCode: String,
    val stateProvince: String?,
    val webPages: List<String>,
    val domains: List<String>,
)

enum class ListingMessage {
    UnableToLoad,
    ShowingCachedData,
    CacheWriteFailed,
}

sealed interface ListingEffect {
    data class OpenDetails(val university: SelectedUniversityArgs) : ListingEffect
}
