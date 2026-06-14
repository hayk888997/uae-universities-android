package com.d3vly.core.data.remote

import com.google.gson.annotations.SerializedName

data class UniversityDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("alpha_two_code")
    val alphaTwoCode: String?,
    @SerializedName("state-province")
    val stateProvince: String?,
    @SerializedName("web_pages")
    val webPages: List<String>?,
    @SerializedName("domains")
    val domains: List<String>?,
)
