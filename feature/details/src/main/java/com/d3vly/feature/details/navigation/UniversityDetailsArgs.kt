package com.d3vly.feature.details.navigation

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UniversityDetailsArgs(
    val name: String,
    val country: String,
    val alphaTwoCode: String,
    val stateProvince: String?,
    val webPages: List<String>,
    val domains: List<String>,
) : Parcelable {
    fun toBundle(): Bundle {
        return Bundle().apply {
            putParcelable(EXTRA_ARGS, this@UniversityDetailsArgs)
        }
    }

    companion object {
        private const val EXTRA_ARGS = "com.d3vly.feature.details.ARGS"
        private val EMPTY = UniversityDetailsArgs(
            name = "",
            country = "",
            alphaTwoCode = "",
            stateProvince = null,
            webPages = emptyList(),
            domains = emptyList(),
        )

        fun from(bundle: Bundle?): UniversityDetailsArgs {
            return bundle?.getDetailsArgs() ?: EMPTY
        }

        @Suppress("DEPRECATION")
        private fun Bundle.getDetailsArgs(): UniversityDetailsArgs? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getParcelable(EXTRA_ARGS, UniversityDetailsArgs::class.java)
            } else {
                getParcelable(EXTRA_ARGS)
            }
        }
    }
}
