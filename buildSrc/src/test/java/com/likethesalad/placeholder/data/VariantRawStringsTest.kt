package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.models.ValuesStrings
import com.likethesalad.placeholder.models.VariantResPaths
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.io.File

class VariantRawStringsTest {

    @Test
    fun `Get raw strings for unflavored variant`() {
        val mainSetDir = getVariantSetDir("main")
        val variantDirsPathFinder = mockk<VariantDirsPathFinder>()
        every { variantDirsPathFinder.existingPathsResDirs }.returns(
            listOf(
                VariantResPaths("main", mainSetDir)
            )
        )

        val variantRawStrings = VariantRawStrings(variantDirsPathFinder)

        Truth.assertThat(variantRawStrings.getValuesStrings()).containsExactly(
            ValuesStrings(
                "values",
                listOf(
                    StringResourceModel("welcome_1", "The welcome message for TesT1"),
                    StringResourceModel("message_1", "The message TesT1")
                ),
                null
            )
        )
    }

    private fun getVariantSetDir(variantName: String): Set<File> {
        return setOf(File(javaClass.getResource("raw/variantMock/$variantName/res").file))
    }
}