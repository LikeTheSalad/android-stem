package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.utils.AndroidExtensionHelper
import com.likethesalad.placeholder.utils.AppVariantHelper
import com.likethesalad.placeholder.utils.ConfigurationProvider
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
        val buildDirProvider = mockk<BuildDirProvider>()
        val appVariantHelper = mockk<AppVariantHelper>()
        val androidExtensionHelper = mockk<AndroidExtensionHelper>()
        val oldSrcDir = temporaryFolder.newFolder("res")
        val oldSrcDir2 = temporaryFolder.newFolder("res2")
        val newSrcDir = File(buildDir, "generated/resolved/$variantName")
        val configurationProvider = mockk<ConfigurationProvider>()
        val variantSourceSets = setOf(oldSrcDir, oldSrcDir2)
        every { configurationProvider.keepResolvedFiles() }.returns(false)
        every { buildDirProvider.getBuildDir() }.returns(buildDir)
        every { appVariantHelper.getVariantName() }.returns(variantName)
        every { androidExtensionHelper.getVariantSrcDirs(variantName) }.returns(variantSourceSets)
        val variantBuildResolvedDir = VariantBuildResolvedDir(
            appVariantHelper,
            buildDirProvider,
            configurationProvider,
            androidExtensionHelper
        )

        Truth.assertThat(variantBuildResolvedDir.resolvedDir).isEqualTo(
            newSrcDir
        )
        verify {
            androidExtensionHelper.setVariantSrcDirs(
                variantName,
                setOf(oldSrcDir, oldSrcDir2, newSrcDir)
            )
        }
    }

    @Test
    fun `Get build res dir for variant and keep it`() {
        val variantName = "demoDebug"
        val buildDir = mockk<File>()
        val buildDirProvider = mockk<BuildDirProvider>()
        val appVariantHelper = mockk<AppVariantHelper>()
        val androidExtensionHelper = mockk<AndroidExtensionHelper>()
        val configurationProvider = mockk<ConfigurationProvider>()
        every { configurationProvider.keepResolvedFiles() }.returns(true)
        every { buildDirProvider.getBuildDir() }.returns(buildDir)
        every { appVariantHelper.getVariantName() }.returns(variantName)
        every { androidExtensionHelper.getVariantSrcDirs(variantName) }.returns(
            getVariantSourceSet(
                variantName,
                "res"
            )
        )

        val variantBuildResolvedDir = VariantBuildResolvedDir(
            appVariantHelper,
            buildDirProvider,
            configurationProvider,
            androidExtensionHelper
        )

        Truth.assertThat(variantBuildResolvedDir.resolvedDir).isEqualTo(
            File(temporaryFolder.root, "src/$variantName/res/")
        )
    }

    private fun getVariantSourceSet(variantName: String, vararg resDirNames: String): Set<File> {
        val resDirs = resDirNames.map { temporaryFolder.newFolder("src", variantName, it) }
        return resDirs.toSet()
    }
}