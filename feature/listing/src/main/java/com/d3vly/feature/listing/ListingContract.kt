package com.d3vly.feature.listing

import androidx.annotation.StringRes
import com.d3vly.core.domain.model.University

sealed interface ListingIntent {
    data object Load : ListingIntent
    data object Refresh : ListingIntent
    data class UniversityClicked(val university: University) : ListingIntent
}

data class ListingState(
    val isLoading: Boolean = false,
    val universities: List<University> = emptyList(),
    @StringRes val errorMessageRes: Int? = null,
)

sealed interface ListingEffect {
    data class OpenDetails(val university: University) : ListingEffect
}
