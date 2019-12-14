package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.AndroidProjectHelper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
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

class VariantDirsPathFinderTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    lateinit var androidExtensionWrapper: AndroidExtensionWrapper
    lateinit var variantDirsPathResolver: VariantDirsPathResolver
    lateinit var variantDirsPathFinder: VariantDirsPathFinder
    lateinit var srcDir: File
    lateinit var srcPath: String

    @Before
    fun setUp() {
        srcDir = temporaryFolder.newFolder("src")
        srcPath = srcDir.absolutePath
        androidExtensionWrapper = mockk()
        val androidProjectHelper = mockk<AndroidProjectHelper>()
        every { androidProjectHelper.androidExtension }.returns(androidExtensionWrapper)
        variantDirsPathResolver = mockk()
        variantDirsPathFinder = VariantDirsPathFinder(variantDirsPathResolver, androidProjectHelper)
    }

    @Test
    fun `Get path tree filtered by available dirs`() {
        // Given
        val resolvedPaths = listOf("main", "demo", "full", "else", "another")
        val mainFolders = createResFolderFor("main", "res", "res2")
        val fullFolders = createResFolderFor("full", "res")
        val anotherFolders = createResFolderFor("another", "res", "res1")
        every { variantDirsPathResolver.pathList }.returns(resolvedPaths)
        every { androidExtensionWrapper.getSourceSets() }.returns(
            getSourceSetsMap(
                mapOf(
                    "main" to mainFolders,
                    "demo" to emptyList(),
                    "full" to fullFolders,
                    "else" to emptyList(),
                    "another" to anotherFolders
                )
            )
        )

        // When
        val result = variantDirsPathFinder.existingPathsResDirs

        // Then
        Truth.assertThat(result).containsExactly(
            VariantResPaths(
                "main",
                mainFolders.toSet()
            ),
            VariantResPaths(
                "full",
                fullFolders.toSet()
            ),
            VariantResPaths(
                "another",
                anotherFolders.toSet()
            )
        ).inOrder()
    }

    private fun createResFolderFor(path: String, vararg resFolderNames: String): List<File> {
        val fileList = mutableListOf<File>()
        for (resName in resFolderNames) {
            fileList.add(temporaryFolder.newFolder("src", path, resName))
        }
        return fileList
    }

    private fun getSourceSetsMap(existingPaths: Map<String, List<File>>):
            Map<String, AndroidSourceSetWrapper> {
        val result = mutableMapOf<String, AndroidSourceSetWrapper>()
        for ((k, v) in existingPaths) {
            result[k] = getSourceSets(k, v.toSet())
        }
        return result
    }

    private fun getSourceSets(
        name: String,
        dirs: Set<File>
    ): AndroidSourceSetWrapper {
        val sourceSet = mockk<AndroidSourceSetWrapper>()
        val dirsWrapper = mockk<AndroidSourceDirectorySetWrapper>()
        every { dirsWrapper.getSrcDirs() }.returns(dirs)
        every { sourceSet.getName() }.returns(name)
        every { sourceSet.getRes() }.returns(dirsWrapper)
        return sourceSet
    }
}