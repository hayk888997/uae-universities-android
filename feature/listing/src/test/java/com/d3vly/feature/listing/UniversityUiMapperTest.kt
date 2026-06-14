package com.d3vly.feature.listing

import com.d3vly.core.domain.model.University
import com.d3vly.feature.listing.navigation.SelectedUniversityArgs
import org.junit.Assert.assertEquals
import org.junit.Test

class UniversityUiMapperTest {
    @Test
    fun `university maps to ui model`() {
        val university = university()

        val uiModel = university.toUiModel()

        assertEquals(universityUiModel(), uiModel)
    }

    @Test
    fun `ui model maps to selected university args`() {
        val uiModel = universityUiModel()

        val args = uiModel.toSelectedUniversityArgs()

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

    private fun universityUiModel(): UniversityUiModel {
        return UniversityUiModel(
            name = "Abu Dhabi University",
            country = "United Arab Emirates",
            alphaTwoCode = "AE",
            stateProvince = "Abu Dhabi",
            webPages = listOf("https://www.adu.ac.ae"),
            domains = listOf("adu.ac.ae"),
        )
    }
}
