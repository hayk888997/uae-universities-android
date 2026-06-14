package com.d3vly.feature.listing.navigation

import android.os.Parcelable
import com.d3vly.core.domain.model.University
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectedUniversityArgs(
    val name: String,
    val country: String,
    val alphaTwoCode: String,
    val stateProvince: String?,
    val webPages: List<String>,
    val domains: List<String>,
) : Parcelable {
    fun toUniversity(): University {
        return University(
            name = name,
            country = country,
            alphaTwoCode = alphaTwoCode,
            stateProvince = stateProvince,
            webPages = webPages,
            domains = domains,
        )
    }

    companion object {
        fun from(university: University): SelectedUniversityArgs {
            return SelectedUniversityArgs(
                name = university.name,
                country = university.country,
                alphaTwoCode = university.alphaTwoCode,
                stateProvince = university.stateProvince,
                webPages = university.webPages,
                domains = university.domains,
            )
        }
    }
}
