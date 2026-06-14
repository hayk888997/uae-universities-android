package com.d3vly.core.domain.model

data class UniversityLoadResult(
    val universities: List<University>,
    val source: UniversityLoadSource,
    val cacheWriteFailed: Boolean = false,
)

sealed interface UniversitiesResult {
    data class Success(val result: UniversityLoadResult) : UniversitiesResult
    data class Error(val cause: Throwable) : UniversitiesResult
}

enum class UniversityLoadSource {
    Remote,
    Cache,
}
