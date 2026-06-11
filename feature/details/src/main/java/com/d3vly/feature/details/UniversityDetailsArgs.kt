package com.d3vly.feature.details

import android.content.Intent
import android.os.Build
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
    fun putInto(intent: Intent): Intent {
        return intent.putExtra(EXTRA_ARGS, this)
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

        fun from(intent: Intent): UniversityDetailsArgs {
            return intent.getDetailsArgs() ?: EMPTY
        }

        @Suppress("DEPRECATION")
        private fun Intent.getDetailsArgs(): UniversityDetailsArgs? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getParcelableExtra(EXTRA_ARGS, UniversityDetailsArgs::class.java)
            } else {
                getParcelableExtra(EXTRA_ARGS)
            }
        }
    }
}
