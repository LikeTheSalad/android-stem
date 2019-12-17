package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
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
    private lateinit var androidExtensionWrapper: AndroidExtensionWrapper
    private lateinit var pathIdentityResolver: PathIdentityResolver

    @Before
    fun setup() {
        srcDir = temporaryFolder.newFolder(srcDirName)
        androidExtensionWrapper = mockk()
        pathIdentityResolver = PathIdentityResolver(androidExtensionWrapper)
    }

    @Test
    fun `Get resolved strings file`() {
        val variantName = "clientDebug"
        val sourceSet = getVariantSourceSet(variantName, "res")
        every { androidExtensionWrapper.getSourceSets() }.returns(mapOf(variantName to sourceSet))
        val pathIdentity = PathIdentity(variantName, "values", "")

        Truth.assertThat(pathIdentityResolver.getRawStringsFile(pathIdentity).absolutePath)
            .isEqualTo(File(srcDir, "clientDebug/res/values/resolved.xml").absolutePath)
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