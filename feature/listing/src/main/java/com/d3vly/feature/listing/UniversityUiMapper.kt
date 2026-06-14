package com.d3vly.feature.listing

import com.d3vly.core.domain.model.University
import com.d3vly.feature.listing.navigation.SelectedUniversityArgs

fun University.toUiModel(): UniversityUiModel {
    return UniversityUiModel(
        name = name,
        country = country,
        alphaTwoCode = alphaTwoCode,
        stateProvince = stateProvince,
        webPages = webPages,
        domains = domains,
    )
}

fun UniversityUiModel.toSelectedUniversityArgs(): SelectedUniversityArgs {
    return SelectedUniversityArgs(
        name = name,
        country = country,
        alphaTwoCode = alphaTwoCode,
        stateProvince = stateProvince,
        webPages = webPages,
        domains = domains,
    )
}
