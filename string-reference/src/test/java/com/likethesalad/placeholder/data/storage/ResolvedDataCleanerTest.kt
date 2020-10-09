package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.VariantDirsPathFinder
import com.likethesalad.placeholder.models.VariantResPaths
import com.likethesalad.placeholder.utils.AppVariantHelper
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ResolvedDataCleanerTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `Remove all resolved xml files from all res dirs of the buildVariant folder`() {
        // Given
        val variantDirsPathFinder = mockk<VariantDirsPathFinder>()
        val variantName = "clientDebug"
        val otherVariantName = "client"
        val variantResPath = mockk<VariantResPaths>()
        val otherVariantResPath = mockk<VariantResPaths>()
        val appVariantHelper = mockk<AppVariantHelper>()
        val resDir = getResDirWithXmlFiles(
            variantName, "res",
            mapOf(
                "values" to listOf("resolved.xml", "something.xml"),
                "values-es" to listOf("resolved.xml")
            )
        )
        val resDir2 = getResDirWithXmlFiles(
            variantName, "res2",
            mapOf(
                "values-it" to listOf("resolved.xml", "res2valueFile.xml")
            )
        )
        val otherResDir = getResDirWithXmlFiles(
            otherVariantName, "res",
            mapOf(
                "values" to listOf("resolved.xml", "something.xml"),
                "values-es" to listOf("resolved.xml")
            )
        )
        every { appVariantHelper.getVariantName() }.returns(variantName)
        every { variantResPath.paths }.returns(setOf(resDir, resDir2))
        every { variantResPath.variantName }.returns(variantName)
        every { otherVariantResPath.paths }.returns(setOf(otherResDir))
        every { otherVariantResPath.variantName }.returns(otherVariantName)
        every { variantDirsPathFinder.getExistingPathsResDirs() }.returns(
            listOf(
                variantResPath,
                otherVariantResPath
            )
        )
        assertFileExist(resDir, "values/resolved.xml", true)
        assertFileExist(resDir, "values/something.xml", true)
        assertFileExist(resDir, "values-es/resolved.xml", true)
        assertFileExist(resDir2, "values-it/res2valueFile.xml", true)
        assertFileExist(resDir2, "values-it/resolved.xml", true)
        assertFileExist(otherResDir, "values/resolved.xml", true)
        assertFileExist(otherResDir, "values/something.xml", true)
        assertFileExist(otherResDir, "values-es/resolved.xml", true)

        // When
        val resolvedDataCleaner = ResolvedDataCleaner(appVariantHelper, variantDirsPathFinder)
        resolvedDataCleaner.removeResolvedFiles()

        // Then
        assertFileExist(resDir, "values/resolved.xml", false)
        assertFileExist(resDir, "values/something.xml", true)
        assertFileExist(resDir, "values-es/resolved.xml", false)
        assertFileExist(resDir2, "values-it/res2valueFile.xml", true)
        assertFileExist(resDir2, "values-it/resolved.xml", false)
        assertFileExist(otherResDir, "values/resolved.xml", true)
        assertFileExist(otherResDir, "values/something.xml", true)
        assertFileExist(otherResDir, "values-es/resolved.xml", true)
    }

    private fun assertFileExist(resDir: File, fileRelativePath: String, fileExist: Boolean) {
        Truth.assertThat(File(resDir, fileRelativePath).exists()).isEqualTo(fileExist)
    }

    private fun getResDirWithXmlFiles(
        variantName: String,
        resDirName: String,
        valuesFoldersWithFiles: Map<String, List<String>>
    ): File {
        val resDir = temporaryFolder.newFolder(
            "src", variantName, resDirName
        )

        for ((valuesFolderName, valuesFiles) in valuesFoldersWithFiles) {
            temporaryFolder.newFolder("src", variantName, resDirName, valuesFolderName)

            for (fileName in valuesFiles) {
                temporaryFolder.newFile("src/$variantName/$resDirName/$valuesFolderName/$fileName")
            }
        }

        return resDir
    }
}