package com.d3vly.core.domain.model

data class UniversityLoadResult(
    val universities: List<University>,
    val source: UniversityLoadSource,
    val cacheWriteFailed: Boolean = false,
)

enum class UniversityLoadSource {
    Remote,
    Cache,
}
