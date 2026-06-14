package com.d3vly.feature.details.navigation

import com.d3vly.core.domain.model.University

fun University.toUniversityDetailsArgs(): UniversityDetailsArgs {
    return UniversityDetailsArgs(
        name = name,
        country = country,
        alphaTwoCode = alphaTwoCode,
        stateProvince = stateProvince,
        webPages = webPages,
        domains = domains,
    )
}
