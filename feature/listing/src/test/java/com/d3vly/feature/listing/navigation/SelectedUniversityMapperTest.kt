package com.d3vly.feature.listing.navigation

import com.d3vly.core.domain.model.University
import org.junit.Assert.assertEquals
import org.junit.Test

class SelectedUniversityMapperTest {
    @Test
    fun `university maps to selected university args`() {
        val university = university()

        val args = university.toSelectedUniversityArgs()

        assertEquals(
            SelectedUniversityArgs(
                name = "Abu Dhabi University",
                country = "United Arab Emirates",
                alphaTwoCode = "AE",
                stateProvince = "Abu Dhabi",
                webPages = listOf("https://www.adu.ac.ae"),
                domains = listOf("adu.ac.ae"),
            ),
            args,
        )
    }

    @Test
    fun `selected university args map to university`() {
        val args = SelectedUniversityArgs(
            name = "Abu Dhabi University",
            country = "United Arab Emirates",
            alphaTwoCode = "AE",
            stateProvince = "Abu Dhabi",
            webPages = listOf("https://www.adu.ac.ae"),
            domains = listOf("adu.ac.ae"),
        )

        val university = args.toUniversity()

        assertEquals(university(), university)
    }

    private fun university(): University {
        return University(
            name = "Abu Dhabi University",
            country = "United Arab Emirates",
            alphaTwoCode = "AE",
            stateProvince = "Abu Dhabi",
            webPages = listOf("https://www.adu.ac.ae"),
            domains = listOf("adu.ac.ae"),
        )
    }
}
