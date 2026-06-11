package com.d3vly.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "universities")
data class UniversityEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val country: String,
    val alphaTwoCode: String,
    val stateProvince: String?,
    val webPages: List<String>,
    val domains: List<String>,
)
