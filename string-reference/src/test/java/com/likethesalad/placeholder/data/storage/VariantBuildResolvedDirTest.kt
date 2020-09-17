package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceDirectorySetWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceSetWrapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class VariantBuildResolvedDirTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `Get build res dir for variant and don't keep it`() {
        val variantName = "demoDebug"
        val buildDir = temporaryFolder.newFolder("build")
        val androidExtensionWrapper = mockk<AndroidExtensionWrapper>()
        val variantSourceSets = mockk<AndroidSourceSetWrapper>()
        val variantSourceDirectorySetWrapper = mockk<AndroidSourceDirectorySetWrapper>(relaxUnitFun = true)
        val oldSrcDir = temporaryFolder.newFolder("res")
        val oldSrcDir2 = temporaryFolder.newFolder("res2")
        val newSrcDir = File(buildDir, "generated/resolved/$variantName")
        every { variantSourceDirectorySetWrapper.getSrcDirs() }.returns(setOf(oldSrcDir, oldSrcDir2))
        every { variantSourceSets.getRes() }.returns(variantSourceDirectorySetWrapper)
        every { androidExtensionWrapper.getSourceSets() }.returns(mapOf(variantName to variantSourceSets))
        val variantBuildResolvedDir = VariantBuildResolvedDir(
            variantName, buildDir,
            androidExtensionWrapper,
            false
        )

        Truth.assertThat(variantBuildResolvedDir.resolvedDir).isEqualTo(
            newSrcDir
        )
        verify { variantSourceDirectorySetWrapper.setSrcDirs(setOf(oldSrcDir, oldSrcDir2, newSrcDir)) }
    }

    @Test
    fun `Get build res dir for variant and keep it`() {
        val variantName = "demoDebug"
        val buildDir = mockk<File>()
        val androidExtensionWrapper = mockk<AndroidExtensionWrapper>()

        every { androidExtensionWrapper.getSourceSets() }.returns(
            mapOf(
                variantName to getVariantSourceSet(
                    variantName,
                    "res"
                )
            )
        )

        val variantBuildResolvedDir = VariantBuildResolvedDir(
            variantName,
            buildDir,
            androidExtensionWrapper,
            true
        )

        Truth.assertThat(variantBuildResolvedDir.resolvedDir).isEqualTo(
            File(temporaryFolder.root, "src/$variantName/res/")
        )
    }

    private fun getVariantSourceSet(variantName: String, vararg resDirNames: String): AndroidSourceSetWrapper {
        val resDirs = resDirNames.map { temporaryFolder.newFolder("src", variantName, it) }
        val sourceDirSet = mockk<AndroidSourceDirectorySetWrapper>()
        every { sourceDirSet.getSrcDirs() }.returns(resDirs.toSet())

        val sourceSet = mockk<AndroidSourceSetWrapper>()
        every { sourceSet.getRes() }.returns(sourceDirSet)
        return sourceSet
    }
}