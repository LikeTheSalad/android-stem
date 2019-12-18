package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.AndroidVariantHelper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceDirectorySetWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceSetWrapper
import com.likethesalad.placeholder.models.PathIdentity
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class PathIdentityResolverTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val srcDirName = "src"
    private lateinit var srcDir: File
    private lateinit var incrementalDir: File
    private lateinit var androidExtensionWrapper: AndroidExtensionWrapper
    private lateinit var androidVariantHelper: AndroidVariantHelper
    private lateinit var pathIdentityResolver: PathIdentityResolver

    @Before
    fun setup() {
        srcDir = temporaryFolder.newFolder(srcDirName)
        incrementalDir = temporaryFolder.newFolder("build", "incremental", "taskName")
        androidVariantHelper = mockk()
        every { androidVariantHelper.incrementalDir }.returns(incrementalDir.absolutePath)
        androidExtensionWrapper = mockk()
        pathIdentityResolver = PathIdentityResolver(androidExtensionWrapper, androidVariantHelper)
    }

    @Test
    fun `Get resolved strings file`() {
        val variantName = "clientDebug"
        val sourceSet = getVariantSourceSet(variantName, "res")
        every { androidExtensionWrapper.getSourceSets() }.returns(mapOf(variantName to sourceSet))

        assertResolvedStringsFilePath(
            PathIdentity(variantName, "values", ""),
            "clientDebug/res/values/resolved.xml"
        )
        assertResolvedStringsFilePath(
            PathIdentity(variantName, "values-es", "-es"),
            "clientDebug/res/values-es/resolved.xml"
        )
    }

    @Test
    fun `Get resolved strings file when there's more than a res folder`() {
        val variantName = "clientDebug"
        val sourceSet = getVariantSourceSet(variantName, "res1", "res2")
        every { androidExtensionWrapper.getSourceSets() }.returns(mapOf(variantName to sourceSet))

        assertResolvedStringsFilePath(
            PathIdentity(variantName, "values", ""),
            "clientDebug/res1/values/resolved.xml"
        )
        assertResolvedStringsFilePath(
            PathIdentity(variantName, "values-es", "-es"),
            "clientDebug/res1/values-es/resolved.xml"
        )
    }

    @Test
    fun `Get raw gathered strings file`() {
        assertRawStringsFilePath(
            PathIdentity("client", "values", ""),
            "strings/strings.json"
        )
        assertRawStringsFilePath(
            PathIdentity("client", "values-es", "-es"),
            "strings/strings-es.json"
        )
    }

    private fun assertRawStringsFilePath(pathIdentity: PathIdentity, expectedRelativePath: String) {
        Truth.assertThat(pathIdentityResolver.getRawStringsFile(pathIdentity).absolutePath)
            .isEqualTo(File(incrementalDir, expectedRelativePath).absolutePath)
    }

    private fun assertResolvedStringsFilePath(pathIdentity: PathIdentity, expectedRelativePath: String) {
        Truth.assertThat(pathIdentityResolver.getResolvedStringsFile(pathIdentity).absolutePath)
            .isEqualTo(File(srcDir, expectedRelativePath).absolutePath)
    }

    private fun getVariantSourceSet(variantName: String, vararg resDirNames: String): AndroidSourceSetWrapper {
        val resDirs = resDirNames.map { temporaryFolder.newFolder(srcDirName, variantName, it) }
        val sourceDirSet = mockk<AndroidSourceDirectorySetWrapper>()
        every { sourceDirSet.getSrcDirs() }.returns(resDirs.toSet())

        val sourceSet = mockk<AndroidSourceSetWrapper>()
        every { sourceSet.getRes() }.returns(sourceDirSet)
        return sourceSet
    }
}