package com.likethesalad.placeholder.data.storage

import com.likethesalad.placeholder.data.Constants.Companion.RESOLVED_FILE_NAME
import com.likethesalad.placeholder.data.VariantDirsPathFinder
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceDirectorySetWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceSetWrapper
import com.likethesalad.placeholder.models.VariantResPaths
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ResolvedDataCleanerTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var variantDirsPathFinder: VariantDirsPathFinder
    private lateinit var resolvedDataCleaner: ResolvedDataCleaner

    @Before
    fun setup() {
        resolvedDataCleaner = ResolvedDataCleaner(variantDirsPathFinder)
    }

    @Test
    fun `Remove all resolved xml files from all res dirs of all variants listed on this buildVariant`() {
        val variantName = "clientDebug"
        val listOfDirsForThisVariant = listOf("main", "client", "clientDebug", "debug")
        every { variantDirsPathFinder.existingPathsResDirs }
    }

    private fun getResDirsWithResolvedXmlFile(
        variantName: String,
        valuesFolderName: String,
        resDirsWithResolvedFile: Map<String, Boolean>
    ): VariantResPaths {
        val androidSourceSetWrapper = mockk<AndroidSourceSetWrapper>()
        val androidSourceDirSetWrapper = mockk<AndroidSourceDirectorySetWrapper>()
        val resDirs = mutableSetOf<File>()

        for ((resDirName, addResolvedFile) in resDirsWithResolvedFile) {
            val resDir = temporaryFolder.newFolder("src", variantName, resDirName, valuesFolderName)
            if (addResolvedFile) {
                temporaryFolder
                    .newFile("src/$variantName/$resDirName/$valuesFolderName/$RESOLVED_FILE_NAME")
            }
            resDirs.add(resDir)
        }

        every { androidSourceDirSetWrapper.getSrcDirs() }.returns(resDirs)
        every { androidSourceSetWrapper.getRes() }.returns(androidSourceDirSetWrapper)

//        return androidSourceSetWrapper
        TODO()
    }
}