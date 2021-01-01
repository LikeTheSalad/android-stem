package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.android.AppVariantHelper
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantDirsPathFinder
import com.likethesalad.placeholder.modules.common.models.VariantResPaths
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.dirs.VariantValuesFolders
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.dirs.VariantValuesFoldersFactory
import com.likethesalad.placeholder.modules.resolveStrings.data.helpers.files.ResolvedDataCleaner
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
        val variantValuesFoldersFactory = VariantValuesFoldersFactory()
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
        assertFileExists(resDir, "values/resolved.xml", true)
        assertFileExists(resDir, "values/something.xml", true)
        assertFileExists(resDir, "values-es/resolved.xml", true)
        assertFileExists(resDir2, "values-it/res2valueFile.xml", true)
        assertFileExists(resDir2, "values-it/resolved.xml", true)
        assertFileExists(otherResDir, "values/resolved.xml", true)
        assertFileExists(otherResDir, "values/something.xml", true)
        assertFileExists(otherResDir, "values-es/resolved.xml", true)

        // When
        val resolvedDataCleaner = ResolvedDataCleaner(
            appVariantHelper,
            variantDirsPathFinder,
            variantValuesFoldersFactory
        )
        resolvedDataCleaner.removeResolvedFiles()

        // Then
        assertFileExists(resDir, "values/resolved.xml", false)
        assertFileExists(resDir, "values/something.xml", true)
        assertFileExists(resDir, "values-es/resolved.xml", false)
        assertFileExists(resDir2, "values-it/res2valueFile.xml", true)
        assertFileExists(resDir2, "values-it/resolved.xml", false)
        assertFileExists(otherResDir, "values/resolved.xml", true)
        assertFileExists(otherResDir, "values/something.xml", true)
        assertFileExists(otherResDir, "values-es/resolved.xml", true)
    }

    private fun assertFileExists(resDir: File, fileRelativePath: String, fileExist: Boolean) {
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