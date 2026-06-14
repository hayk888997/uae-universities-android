package com.d3vly.core.data.mapper

import com.d3vly.core.data.local.UniversityEntity
import com.d3vly.core.data.remote.UniversityDto
import com.d3vly.core.domain.model.University
import org.junit.Assert.assertEquals
import org.junit.Test

class UniversityMapperTest {
    @Test
    fun `dto maps nullable fields to safe domain defaults`() {
        val dto = UniversityDto(
            name = null,
            country = null,
            alphaTwoCode = null,
            stateProvince = null,
            webPages = null,
            domains = null,
        )

        assertEquals(
            University(
                name = "",
                country = "",
                alphaTwoCode = "",
                stateProvince = null,
                webPages = emptyList(),
                domains = emptyList(),
            ),
            dto.toDomain(),
        )
    }

    @Test
    fun `domain maps to entity and back without losing displayed fields`() {
        val university = University(
            name = "Abu Dhabi University",
            country = COUNTRY,
            alphaTwoCode = ALPHA_TWO_CODE,
            stateProvince = null,
            webPages = listOf("https://www.adu.ac.ae"),
            domains = listOf("adu.ac.ae"),
        )

        val entity = university.toEntity()

        assertEquals("abu dhabi university|united arab emirates", entity.id)
        assertEquals(university, entity.toDomain())
    }

    @Test
    fun `entity maps to domain`() {
        val entity = UniversityEntity(
            id = "zayed university|united arab emirates",
            name = "Zayed University",
            country = COUNTRY,
            alphaTwoCode = ALPHA_TWO_CODE,
            stateProvince = "Dubai",
            webPages = listOf("https://www.zu.ac.ae"),
            domains = listOf("zu.ac.ae"),
        )

        assertEquals(
            University(
                name = "Zayed University",
                country = COUNTRY,
                alphaTwoCode = ALPHA_TWO_CODE,
                stateProvince = "Dubai",
                webPages = listOf("https://www.zu.ac.ae"),
                domains = listOf("zu.ac.ae"),
            ),
            entity.toDomain(),
        )
    }

    private companion object {
        const val COUNTRY = "United Arab Emirates"
        const val ALPHA_TWO_CODE = "AE"
    }
}
