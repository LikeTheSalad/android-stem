package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.storage.libraries.LibrariesValuesStringsProvider
import com.likethesalad.placeholder.data.storage.utils.ValuesStringsProvider
import com.likethesalad.placeholder.models.ValuesStrings
import com.likethesalad.placeholder.models.VariantResPaths
import com.likethesalad.placeholder.models.VariantXmlFiles
import com.likethesalad.placeholder.testutils.TestResourcesHandler
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.io.File

class VariantRawStringsTest {

    private val resourcesHandler = TestResourcesHandler(javaClass)
    private lateinit var valuesStringsProvider: ValuesStringsProvider
    private lateinit var librariesValuesStringsProvider: LibrariesValuesStringsProvider

    @Before
    fun setUp() {
        valuesStringsProvider = mockk()
        librariesValuesStringsProvider = mockk()
    }

    @Test
    fun `Get raw strings for unflavored variant`() {
        val mainResDirs = getVariantMapDir("main", "res")
        val libValuesStrings = getLibValuesStringsMock("values", null)
        val variantDirsPathFinder = getVariantDirsPathFinder(
            mapOf(
                "main" to mainResDirs
            )
        )
        val mainVariantXmlFiles = getVariantXmlFiles(
            "main",
            mainResDirs,
            mapOf(
                "values" to mapOf(
                    "res" to listOf("strings.xml")
                )
            )
        )
        val mainValuesStrings = getValuesStringsMock(
            "values", libValuesStrings,
            mainVariantXmlFiles
        )

        val variantRawStrings = createVariantRawStrings(variantDirsPathFinder)

        Truth.assertThat(variantRawStrings.valuesStrings).containsExactly(
            mainValuesStrings
        )
    }

    @Test
    fun `Get raw strings for unflavored variant with multiple res dirs`() {
        val mainResDirs = getVariantMapDir("main", "res", "res2")
        val libValuesStrings = getLibValuesStringsMock("values", null)
        val variantDirsPathFinder = getVariantDirsPathFinder(
            mapOf(
                "main" to mainResDirs
            )
        )
        val mainVariantXmlFiles = getVariantXmlFiles(
            "main",
            mainResDirs,
            mapOf(
                "values" to mapOf(
                    "res" to listOf("strings.xml"),
                    "res2" to listOf("strings.xml")
                )
            )
        )
        val mainValuesStrings = getValuesStringsMock(
            "values", libValuesStrings,
            mainVariantXmlFiles
        )

        val variantRawStrings = createVariantRawStrings(variantDirsPathFinder)

        Truth.assertThat(variantRawStrings.valuesStrings).containsExactly(
            mainValuesStrings
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
        val mainVariantXmlFiles = getVariantXmlFiles(
            "main", mainResDirs,
            mapOf(
                "values" to mapOf(
                    "resMultilingual" to listOf("strings.xml")
                ),
                "values-es" to mapOf(
                    "resMultilingual" to listOf("strings_es.xml")
                )
            )
        )
        val baseLibValuesStrings = getLibValuesStringsMock("values", null)
        val baseValuesStrings = getValuesStringsMock(
            "values", baseLibValuesStrings,
            mainVariantXmlFiles
        )
        val esLibValuesStrings = getLibValuesStringsMock("values-es", baseValuesStrings)
        val esValuesStrings = getValuesStringsMock(
            "values-es", esLibValuesStrings,
            mainVariantXmlFiles
        )

        val variantRawStrings = createVariantRawStrings(variantDirsPathFinder)

        Truth.assertThat(variantRawStrings.valuesStrings).containsExactly(
            baseValuesStrings,
            esValuesStrings
        )
    }

    @Test
    fun `Get raw strings for single dimension flavored variant`() {
        val mainVariant = "main"
        val flavor = "client"
        val mainResDirs = getVariantMapDir(mainVariant, "res")
        val flavorResDirs = getVariantMapDir(flavor, "res")
        val variantDirsPathFinder = getVariantDirsPathFinder(
            mapOf(
                mainVariant to mainResDirs,
                flavor to flavorResDirs
            )
        )
        val mainVariantXmlFiles = getVariantXmlFiles(
            mainVariant,
            mainResDirs,
            mapOf(
                "values" to mapOf(
                    "res" to listOf("strings.xml")
                )
            )
        )
        val flavorVariantXmlFiles = getVariantXmlFiles(
            "client",
            flavorResDirs,
            mapOf(
                "values" to mapOf(
                    "res" to listOf("strings.xml")
                )
            )
        )
        val baseLibsValuesStrings = getLibValuesStringsMock("values", null)
        val flavorValuesStrings = getValuesStringsMock(
            "values", baseLibsValuesStrings,
            mainVariantXmlFiles, flavorVariantXmlFiles
        )

        val variantRawStrings = createVariantRawStrings(variantDirsPathFinder)

        Truth.assertThat(variantRawStrings.valuesStrings).containsExactly(
            flavorValuesStrings
        )
    }

    @Test
    fun `Get raw strings for single dimension multilingual base flavored variant`() {
        val mainVariant = "main"
        val flavor = "client"
        val mainResDirs = getVariantMapDir(mainVariant, "res", "resMultilingual")
        val flavorResDirs = getVariantMapDir(flavor, "res")
        val variantDirsPathFinder = getVariantDirsPathFinder(
            mapOf(
                mainVariant to mainResDirs,
                flavor to flavorResDirs
            )
        )
        val mainVariantXmlFiles = getVariantXmlFiles(
            mainVariant,
            mainResDirs, mapOf(
                "values" to mapOf(
                    "res" to listOf("strings.xml"),
                    "resMultilingual" to listOf("strings.xml")
                ),
                "values-es" to mapOf(
                    "resMultilingual" to listOf("strings_es.xml")
                )
            )
        )
        val flavorVariantXmlFiles = getVariantXmlFiles(
            flavor, flavorResDirs,
            mapOf(
                "values" to mapOf(
                    "res" to listOf("strings.xml")
                )
            )
        )
        val baseLibsValuesStrings = getLibValuesStringsMock("values", null)
        val baseValuesStrings = getValuesStringsMock(
            "values", baseLibsValuesStrings,
            mainVariantXmlFiles, flavorVariantXmlFiles
        )
        val langLibsValuesStrings = getLibValuesStringsMock(
            "values-es",
            baseValuesStrings
        )
        val langValuesStrings = getValuesStringsMock(
            "values-es", langLibsValuesStrings,
            mainVariantXmlFiles, flavorVariantXmlFiles
        )

        val variantRawStrings = createVariantRawStrings(variantDirsPathFinder)

        Truth.assertThat(variantRawStrings.valuesStrings).containsExactly(
            baseValuesStrings,
            langValuesStrings
        )
    }

    @Test
    fun `Get raw strings for single dimension multilingual flavor flavored variant`() {
        val mainVariant = "main"
        val flavor = "client"
        val mainResDirs = getVariantMapDir(mainVariant, "res")
        val flavorResDirs = getVariantMapDir(flavor, "res", "resMulti")
        val variantDirsPathFinder = getVariantDirsPathFinder(
            mapOf(
                mainVariant to mainResDirs,
                flavor to flavorResDirs
            )
        )

        val mainVariantXmlFiles = getVariantXmlFiles(
            mainVariant,
            mainResDirs, mapOf(
                "values" to mapOf(
                    "res" to listOf("strings.xml")
                )
            )
        )
        val flavorVariantXmlFiles = getVariantXmlFiles(
            flavor, flavorResDirs,
            mapOf(
                "values" to mapOf(
                    "res" to listOf("strings.xml"),
                    "resMulti" to listOf("strings.xml")
                ),
                "values-es" to mapOf(
                    "resMulti" to listOf("strings_es.xml")
                )
            )
        )
        val baseLibsValuesStrings = getLibValuesStringsMock("values", null)
        val baseValuesStrings = getValuesStringsMock(
            "values", baseLibsValuesStrings,
            mainVariantXmlFiles, flavorVariantXmlFiles
        )
        val langLibsValuesStrings = getLibValuesStringsMock(
            "values-es",
            baseValuesStrings
        )
        val langValuesStrings = getValuesStringsMock(
            "values-es", langLibsValuesStrings,
            mainVariantXmlFiles, flavorVariantXmlFiles
        )

        val variantRawStrings = createVariantRawStrings(variantDirsPathFinder)

        Truth.assertThat(variantRawStrings.valuesStrings).containsExactly(
            baseValuesStrings,
            langValuesStrings
        )
    }

    @Test
    fun `Get raw strings for single dimension multilingual all flavored variant`() {
        val mainVariant = "main"
        val flavor = "client"
        val mainResDirs = getVariantMapDir(mainVariant, "res", "resMultilingual")
        val flavorResDirs = getVariantMapDir(flavor, "res", "resMulti")
        val variantDirsPathFinder = getVariantDirsPathFinder(
            mapOf(
                mainVariant to mainResDirs,
                flavor to flavorResDirs
            )
        )

        val mainVariantXmlFiles = getVariantXmlFiles(
            mainVariant,
            mainResDirs, mapOf(
                "values" to mapOf(
                    "res" to listOf("strings.xml"),
                    "resMultilingual" to listOf("strings.xml")
                ),
                "values-es" to mapOf(
                    "resMultilingual" to listOf("strings_es.xml")
                )
            )
        )
        val flavorVariantXmlFiles = getVariantXmlFiles(
            flavor, flavorResDirs,
            mapOf(
                "values" to mapOf(
                    "res" to listOf("strings.xml"),
                    "resMulti" to listOf("strings.xml")
                ),
                "values-es" to mapOf(
                    "resMulti" to listOf("strings_es.xml")
                )
            )
        )
        val baseLibsValuesStrings = getLibValuesStringsMock("values", null)
        val baseValuesStrings = getValuesStringsMock(
            "values", baseLibsValuesStrings,
            mainVariantXmlFiles, flavorVariantXmlFiles
        )
        val langLibsValuesStrings = getLibValuesStringsMock(
            "values-es",
            baseValuesStrings
        )
        val langValuesStrings = getValuesStringsMock(
            "values-es", langLibsValuesStrings,
            mainVariantXmlFiles, flavorVariantXmlFiles
        )

        val variantRawStrings = createVariantRawStrings(variantDirsPathFinder)

        Truth.assertThat(variantRawStrings.valuesStrings).containsExactly(
            baseValuesStrings,
            langValuesStrings
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

    private fun getVariantXmlFiles(
        variantName: String,
        gatheredResDirs: Map<String, File>,
        valuesToResFiles: Map<String, Map<String, List<String>>>
    ): VariantXmlFiles {
        val filesPerValuesFolder = valuesToResFiles.mapValues { valuesFolderMap ->
            valuesFolderMap.value.map { resDirWithFileNames ->
                resDirWithFileNames.value.map { fileName ->
                    File(gatheredResDirs.getValue(resDirWithFileNames.key), "${valuesFolderMap.key}/$fileName")
                }
            }.flatten()
        }
        return VariantXmlFiles(
            variantName, filesPerValuesFolder.mapValues {
                ValuesXmlFiles(it.value.toSet())
            }
        )
    }

    private fun getLibValuesStringsMock(
        folderName: String,
        parent: ValuesStrings?
    ): ValuesStrings {
        val libValuesStrings = mockk<ValuesStrings>()
        every { librariesValuesStringsProvider.getValuesStringsFor(folderName, parent) }
            .returns(libValuesStrings)
        return libValuesStrings
    }

    private fun getValuesStringsMock(
        folderName: String,
        parentValuesStrings: ValuesStrings,
        vararg variantXmlFiles: VariantXmlFiles
    ): ValuesStrings {
        val valuesStrings = mockk<ValuesStrings>()
        every {
            valuesStringsProvider.getValuesStringsForFolderFromVariants(
                folderName,
                variantXmlFiles.toList(),
                parentValuesStrings
            )
        }.returns(valuesStrings)

        return valuesStrings
    }

    private fun getVariantMapDir(variantName: String, vararg resDirsNames: String): Map<String, File> {
        val dirs = mutableMapOf<String, File>()
        for (resDirName in resDirsNames) {
            dirs[resDirName] =
                resourcesHandler.getResourceFile("raw/variantMock/$variantName/$resDirName")
        }
        return dirs
    }

    private fun createVariantRawStrings(variantDirsPathFinder: VariantDirsPathFinder): VariantRawStrings {
        return VariantRawStrings(variantDirsPathFinder, valuesStringsProvider, librariesValuesStringsProvider)
    }
}