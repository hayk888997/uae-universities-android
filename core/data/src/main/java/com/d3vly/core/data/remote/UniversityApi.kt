package com.d3vly.core.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface UniversityApi {
    @GET("search")
    suspend fun searchUniversities(
        @Query("country") country: String = UniversitySearchConfig.COUNTRY,
    ): List<UniversityDto>
}
