package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantBuildResolvedDir
import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.tools.android.plugin.data.AndroidExtension
import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
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
        val buildDirProvider = mockk<BuildDirProvider>()
        val androidVariantData = mockk<AndroidVariantData>()
        val androidExtension = mockk<AndroidExtension>()
        val androidExtensionProvider = mockk<AndroidExtensionProvider>()
        val oldSrcDir = temporaryFolder.newFolder("res")
        val oldSrcDir2 = temporaryFolder.newFolder("res2")
        val newSrcDir = File(buildDir, "generated/resolved/$variantName")
        val variantSourceSets = setOf(oldSrcDir, oldSrcDir2)
        every { buildDirProvider.getBuildDir() }.returns(buildDir)
        every { androidVariantData.getVariantName() }.returns(variantName)
        every { androidExtension.getVariantSrcDirs(variantName) }.returns(variantSourceSets)
        every { androidExtension.setVariantSrcDirs(any(), any()) } just Runs
        every { androidExtensionProvider.getExtension() }.returns(androidExtension)
        val variantBuildResolvedDir =
            VariantBuildResolvedDir(
                buildDirProvider,
                androidExtensionProvider,
                androidVariantData
            )

        Truth.assertThat(variantBuildResolvedDir.resolvedDir).isEqualTo(
            newSrcDir
        )
        verify {
            androidExtension.setVariantSrcDirs(
                variantName,
                setOf(oldSrcDir, oldSrcDir2, newSrcDir)
            )
        }
    }
}