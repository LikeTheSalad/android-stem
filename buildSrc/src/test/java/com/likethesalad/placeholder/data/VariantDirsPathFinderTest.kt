package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.AndroidProjectHelper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceDirectorySetWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceSetWrapper
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
        createResFolderFor("main")
        createResFolderFor("full")
        createResFolderFor("another")
        every { variantDirsPathResolver.getPath() }.returns(resolvedPaths)
        every { androidExtensionWrapper.getSourceSets() }.returns(getSourceSetsMap(resolvedPaths))

        // When
        val result = variantDirsPathFinder.getExistingPaths()

        // Then
        Truth.assertThat(result.map { it.map { file -> file.absolutePath } }).containsExactly(
            listOf("$srcPath/main/res"),
            listOf("$srcPath/full/res"),
            listOf("$srcPath/another/res")
        )
    }

    private fun createResFolderFor(path: String) {
        temporaryFolder.newFolder("src", path, "res")
    }

    private fun getSourceSetsMap(resolvedPaths: List<String>): Map<String, AndroidSourceSetWrapper> {
        val result = mutableMapOf<String, AndroidSourceSetWrapper>()
        for (it in resolvedPaths) {
            result[it] = getSourceSets(it, setOf(File("$srcPath/$it/res")))
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