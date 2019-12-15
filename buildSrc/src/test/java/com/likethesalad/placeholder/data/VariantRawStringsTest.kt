package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.models.ValuesStrings
import com.likethesalad.placeholder.models.VariantResPaths
import com.likethesalad.placeholder.testutils.TestResourcesHandler
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.io.File

class VariantRawStringsTest {

    private val resourcesHandler = TestResourcesHandler(javaClass)

    @Test
    fun `Get raw strings for unflavored variant`() {
        val mainResDirs = getVariantMapDir("main", "res")
        val variantDirsPathFinder = getVariantDirsPathFinder(
            mapOf(
                "main" to mainResDirs
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

    @Test
    fun `Get raw strings for unflavored variant with multiple res dirs`() {
        val mainResDirs = getVariantMapDir("main", "res", "res2")
        val variantDirsPathFinder = getVariantDirsPathFinder(
            mapOf(
                "main" to mainResDirs
            )
        )

        val variantRawStrings = VariantRawStrings(variantDirsPathFinder)

        Truth.assertThat(variantRawStrings.getValuesStrings()).containsExactly(
            ValuesStrings(
                "values",
                ValuesStringFiles(
                    setOf(
                        File(mainResDirs["res"], "values/strings.xml"),
                        File(mainResDirs["res2"], "values/strings.xml")
                    )
                ),
                null
            )
        )
    }

    @Test
    fun `Get raw strings for unflavored multilingual variant`() {
        val mainResDirs = getVariantMapDir("main", "resMultilingual")
        val variantDirsPathFinder = getVariantDirsPathFinder(
            mapOf(
                "main" to mainResDirs
            )
        )

        val variantRawStrings = VariantRawStrings(variantDirsPathFinder)

        val baseValuesStrings = ValuesStrings(
            "values",
            ValuesStringFiles(
                setOf(
                    File(mainResDirs["resMultilingual"], "values/strings.xml")
                )
            ),
            null
        )
        Truth.assertThat(variantRawStrings.getValuesStrings()).containsExactly(
            baseValuesStrings,
            ValuesStrings(
                "values-es",
                ValuesStringFiles(
                    setOf(File(mainResDirs["resMultilingual"], "values-es/strings_es.xml"))
                ), baseValuesStrings
            )
        )
    }

    private fun getVariantDirsPathFinder(existingVariantsWithDirs: Map<String, Map<String, File>>)
            : VariantDirsPathFinder {
        val variantResPaths = mutableListOf<VariantResPaths>()
        for ((variantName, resMap) in existingVariantsWithDirs) {
            val setOfPaths = resMap.toList().map {
                it.second
            }.toSet()
            variantResPaths.add(VariantResPaths(variantName, setOfPaths))
        }

        val variantDirsPathFinder = mockk<VariantDirsPathFinder>()
        every { variantDirsPathFinder.existingPathsResDirs }.returns(variantResPaths)

        return variantDirsPathFinder
    }

    private fun getVariantMapDir(variantName: String, vararg resDirsNames: String): Map<String, File> {
        val dirs = mutableMapOf<String, File>()
        for (resDirName in resDirsNames) {
            dirs[resDirName] =
                resourcesHandler.getResourceFile("raw/variantMock/$variantName/$resDirName")
        }
        return dirs
    }
}