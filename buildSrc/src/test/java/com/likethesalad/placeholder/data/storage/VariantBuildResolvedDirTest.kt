package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class VariantBuildResolvedDirTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `Get build res dir for variant`() {
        val variantName = "demoDebug"
        val buildDir = temporaryFolder.newFolder("build")
        val variantBuildResolvedDir = VariantBuildResolvedDir(variantName, buildDir)

        Truth.assertThat(variantBuildResolvedDir.resolvedDir).isEqualTo(
            File(buildDir, "generated/resolved/$variantName")
        )
    }
}