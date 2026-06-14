package com.d3vly.testapp

import com.d3vly.feature.details.navigation.UniversityDetailsArgs
import com.d3vly.feature.listing.navigation.SelectedUniversityArgs
import org.junit.Assert.assertEquals
import org.junit.Test

class MainNavigationMapperTest {
    @Test
    fun `selected university args map to details args`() {
        val selectedArgs = SelectedUniversityArgs(
            name = "Abu Dhabi University",
            country = "United Arab Emirates",
            alphaTwoCode = "AE",
            stateProvince = "Abu Dhabi",
            webPages = listOf("https://www.adu.ac.ae"),
            domains = listOf("adu.ac.ae"),
        )

        val detailsArgs = selectedArgs.toDetailsArgs()

        assertEquals(
            UniversityDetailsArgs(
                name = "Abu Dhabi University",
                country = "United Arab Emirates",
                alphaTwoCode = "AE",
                stateProvince = "Abu Dhabi",
                webPages = listOf("https://www.adu.ac.ae"),
                domains = listOf("adu.ac.ae"),
            ),
            detailsArgs,
        )
    }
}
