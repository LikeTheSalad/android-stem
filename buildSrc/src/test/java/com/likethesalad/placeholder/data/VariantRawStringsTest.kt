package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.models.ValuesStrings
import com.likethesalad.placeholder.models.VariantResPaths
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.io.File

class VariantRawStringsTest {

    @Test
    fun `Get raw strings for unflavored variant`() {
        val mainResDirs = getVariantMapDir("main", "res")
        val variantDirsPathFinder = mockk<VariantDirsPathFinder>()
        every { variantDirsPathFinder.existingPathsResDirs }.returns(
            listOf(
                VariantResPaths("main", mainResDirs.values.toSet())
            )
        )

        val variantRawStrings = VariantRawStrings(variantDirsPathFinder)

        Truth.assertThat(variantRawStrings.getValuesStrings()).containsExactly(
            ValuesStrings(
                "values",
                ValuesStringFiles(
                    setOf(
                        File(mainResDirs["res"], "values/strings.xml")
                    )
                ),
                null
            )
        )
    }

    private fun getVariantMapDir(variantName: String, vararg resDirsNames: String): Map<String, File> {
        val dirs = mutableMapOf<String, File>()
        for (resDirName in resDirsNames) {
            dirs[resDirName] =
                File(javaClass.getResource("raw/variantMock/$variantName/$resDirName").file)
        }
        return dirs
    }
}