package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceDirectorySetWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceSetWrapper
import io.mockk.every
import io.mockk.mockk
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
        val variantBuildResolvedDir = VariantBuildResolvedDir(
            variantName, buildDir,
            mockk(), false
        )

        Truth.assertThat(variantBuildResolvedDir.resolvedDir).isEqualTo(
            File(buildDir, "generated/resolved/$variantName")
        )
    }

    @Test
    fun `Get build res dir for variant and keep it`() {
        val variantName = "demoDebug"
        val buildDir = mockk<File>()
        val androidExtensionWrapper = mockk<AndroidExtensionWrapper>()
        val variantBuildResolvedDir = VariantBuildResolvedDir(
            variantName,
            buildDir,
            androidExtensionWrapper,
            true
        )
        every { androidExtensionWrapper.getSourceSets() }.returns(
            mapOf(
                variantName to getVariantSourceSet(
                    variantName,
                    "res"
                )
            )
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