package com.d3vly.core.domain.model

data class UniversityLoadResult(
    val universities: List<University>,
    val source: UniversityLoadSource,
)

enum class UniversityLoadSource {
    Remote,
    Cache,
}
