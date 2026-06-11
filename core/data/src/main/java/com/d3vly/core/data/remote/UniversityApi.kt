package com.d3vly.core.data.remote

import com.d3vly.core.domain.model.UniversitySearchTarget
import retrofit2.http.GET
import retrofit2.http.Query

interface UniversityApi {
    @GET("search")
    suspend fun searchUniversities(
        @Query("country") country: String = UniversitySearchTarget.COUNTRY,
    ): List<UniversityDto>
}
