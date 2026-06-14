package com.d3vly.testapp

import com.d3vly.feature.details.navigation.UniversityDetailsArgs
import com.d3vly.feature.listing.navigation.SelectedUniversityArgs

internal fun SelectedUniversityArgs.toDetailsArgs(): UniversityDetailsArgs {
    return UniversityDetailsArgs(
        name = name,
        country = country,
        alphaTwoCode = alphaTwoCode,
        stateProvince = stateProvince,
        webPages = webPages,
        domains = domains,
    )
}
