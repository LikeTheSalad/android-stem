package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.models.VariantResPaths
import com.likethesalad.placeholder.utils.AndroidExtensionHelper
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

    lateinit var androidExtensionHelper: AndroidExtensionHelper
    lateinit var variantDirsPathResolver: VariantDirsPathResolver
    lateinit var variantDirsPathFinder: VariantDirsPathFinder
    lateinit var srcDir: File
    lateinit var srcPath: String

    @Before
    fun setUp() {
        srcDir = temporaryFolder.newFolder("src")
        srcPath = srcDir.absolutePath
        androidExtensionHelper = mockk()
        variantDirsPathResolver = mockk()
        variantDirsPathFinder = VariantDirsPathFinder(variantDirsPathResolver, androidExtensionHelper)
    }

    @Test
    fun `Get path tree filtered by available dirs`() {
        // Given
        val resolvedPaths = listOf("main", "demo", "full", "else", "another")
        val mainFolders = createResFolderFor("main", "res", "res2")
        val fullFolders = createResFolderFor("full", "res")
        val anotherFolders = createResFolderFor("another", "res", "res1")
        every { variantDirsPathResolver.pathList }.returns(resolvedPaths)
        configureSourceSets(
            mapOf(
                "main" to mainFolders,
                "demo" to emptyList(),
                "full" to fullFolders,
                "else" to emptyList(),
                "another" to anotherFolders
            )
        )

        // When
        val result = variantDirsPathFinder.getExistingPathsResDirs()

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

    @Test
    fun `Get path tree with extra main dirs filtered by available dirs`() {
        // Given
        val resolvedPaths = listOf("main", "demo", "full", "else", "another")
        val mainFolders = createResFolderFor("main", "res", "res2")
        val extraMainFolders = createResFolderFor(
            "someGeneratedFolder",
            "resGen", "resGen2"
        )
        val fullFolders = createResFolderFor("full", "res")
        val anotherFolders = createResFolderFor("another", "res", "res1")
        every { variantDirsPathResolver.pathList }.returns(resolvedPaths)
        configureSourceSets(
            mapOf(
                "main" to mainFolders,
                "demo" to emptyList(),
                "full" to fullFolders,
                "else" to emptyList(),
                "another" to anotherFolders
            )
        )

        // When
        val result = variantDirsPathFinder.getExistingPathsResDirs(extraMainFolders)

        // Then
        Truth.assertThat(result).containsExactly(
            VariantResPaths(
                "main",
                (mainFolders + extraMainFolders).toSet()
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

    private fun configureSourceSets(existingPaths: Map<String, List<File>>) {
        for (entry in existingPaths) {
            every {
                androidExtensionHelper.getVariantSrcDirs(entry.key)
            }.returns(entry.value.toSet())
        }
    }
}