package com.d3vly.core.data.mapper

import com.d3vly.core.data.local.UniversityEntity
import com.d3vly.core.data.remote.UniversityDto
import com.d3vly.core.domain.model.University

fun UniversityDto.toDomain(): University {
    return University(
        name = name.orEmpty(),
        country = country.orEmpty(),
        alphaTwoCode = alphaTwoCode.orEmpty(),
        stateProvince = stateProvince,
        webPages = webPages.orEmpty(),
        domains = domains.orEmpty(),
    )
}

fun University.toEntity(): UniversityEntity {
    return UniversityEntity(
        id = stableCacheId(),
        name = name,
        country = country,
        alphaTwoCode = alphaTwoCode,
        stateProvince = stateProvince,
        webPages = webPages,
        domains = domains,
    )
}

private fun University.stableCacheId(): String {
    return listOf(name, country, webPages.firstOrNull().orEmpty(), domains.firstOrNull().orEmpty())
        .joinToString(separator = "|") { it.trim().lowercase() }
}

fun UniversityEntity.toDomain(): University {
    return University(
        name = name,
        country = country,
        alphaTwoCode = alphaTwoCode,
        stateProvince = stateProvince,
        webPages = webPages,
        domains = domains,
    )
}
