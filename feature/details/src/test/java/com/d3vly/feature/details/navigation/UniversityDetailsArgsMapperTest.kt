package com.d3vly.feature.details.navigation

import com.d3vly.core.domain.model.University
import org.junit.Assert.assertEquals
import org.junit.Test

class UniversityDetailsArgsMapperTest {
    @Test
    fun `university maps to university details args`() {
        val university = University(
            name = "Abu Dhabi University",
            country = "United Arab Emirates",
            alphaTwoCode = "AE",
            stateProvince = "Abu Dhabi",
            webPages = listOf("https://www.adu.ac.ae"),
            domains = listOf("adu.ac.ae"),
        )

        val args = university.toUniversityDetailsArgs()

        assertEquals(
            UniversityDetailsArgs(
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
}
