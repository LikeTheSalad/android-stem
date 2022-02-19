package com.likethesalad.stem.data.storage

import com.google.common.truth.Truth
import com.likethesalad.stem.modules.common.helpers.dirs.SourceSetsHandler
import com.likethesalad.stem.modules.common.helpers.dirs.VariantBuildResolvedDir
import com.likethesalad.stem.providers.ProjectDirsProvider
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
        val projectDirsProvider = mockk<ProjectDirsProvider>()
        val androidVariantData = mockk<AndroidVariantData>()
        val sourceSetsHandler = mockk<SourceSetsHandler>()
        val newSrcDir = File(buildDir, "generated/resolved/$variantName")
        every { projectDirsProvider.getBuildDir() }.returns(buildDir)
        every { androidVariantData.getVariantName() }.returns(variantName)
        every { sourceSetsHandler.addToSourceSets(newSrcDir, variantName) } just Runs
        val variantBuildResolvedDir = VariantBuildResolvedDir(
            projectDirsProvider,
            sourceSetsHandler,
            androidVariantData
        )

        Truth.assertThat(variantBuildResolvedDir.resolvedDir).isEqualTo(
            newSrcDir
        )
        verify {
            sourceSetsHandler.addToSourceSets(newSrcDir, variantName)
        }
    }
}