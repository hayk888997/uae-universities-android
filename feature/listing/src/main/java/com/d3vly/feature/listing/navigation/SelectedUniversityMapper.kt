package com.d3vly.feature.listing.navigation

import com.d3vly.core.domain.model.University

fun University.toSelectedUniversityArgs(): SelectedUniversityArgs {
    return SelectedUniversityArgs(
        name = name,
        country = country,
        alphaTwoCode = alphaTwoCode,
        stateProvince = stateProvince,
        webPages = webPages,
        domains = domains,
    )
}

fun SelectedUniversityArgs.toUniversity(): University {
    return University(
        name = name,
        country = country,
        alphaTwoCode = alphaTwoCode,
        stateProvince = stateProvince,
        webPages = webPages,
        domains = domains,
    )
}
