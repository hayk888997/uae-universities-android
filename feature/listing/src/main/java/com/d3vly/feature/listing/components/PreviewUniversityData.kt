package com.d3vly.feature.listing.components

import com.d3vly.core.domain.model.University

internal val previewUniversity = University(
    name = "Abu Dhabi University",
    country = "United Arab Emirates",
    alphaTwoCode = "AE",
    stateProvince = "Abu Dhabi",
    webPages = listOf("https://www.adu.ac.ae"),
    domains = listOf("adu.ac.ae"),
)

internal val previewUniversities = listOf(
    previewUniversity,
    previewUniversity.copy(
        name = "Zayed University",
        webPages = listOf("https://www.zu.ac.ae"),
        domains = listOf("zu.ac.ae"),
    ),
)
