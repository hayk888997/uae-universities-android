package com.d3vly.feature.listing.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectedUniversityArgs(
    val name: String,
    val country: String,
    val alphaTwoCode: String,
    val stateProvince: String?,
    val webPages: List<String>,
    val domains: List<String>,
) : Parcelable
