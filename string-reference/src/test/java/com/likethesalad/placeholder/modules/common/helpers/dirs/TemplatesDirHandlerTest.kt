package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.google.common.truth.Truth
import com.likethesalad.placeholder.providers.ProjectDirsProvider
import com.likethesalad.tools.resource.api.android.environment.Variant
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class TemplatesDirHandlerTest : BaseMockable() {

    @MockK
    lateinit var variantTree: VariantTree

    @MockK
    lateinit var sourceSetsHandler: SourceSetsHandler

    @MockK
    lateinit var projectDirsProvider: ProjectDirsProvider

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var projectDir: File

    private lateinit var templatesDirHandler: TemplatesDirHandler

    @Before
    fun setUp() {
        projectDir = temporaryFolder.newFolder("project")
        every { projectDirsProvider.getProjectDir() }.returns(projectDir)
        every { sourceSetsHandler.addToSourceSets(any(), any()) } just Runs
        templatesDirHandler = TemplatesDirHandler(variantTree, sourceSetsHandler, projectDirsProvider)
    }

    @Test
    fun `Create resource dirs for all variants except last one`() {
        val mainVariantName = "main"
        val demoVariantName = "demo"
        val demoDebugVariantName = "demoDebug"
        val mainVariant = createVariant(mainVariantName)
        val demoVariant = createVariant(demoVariantName)
        val demoDebugVariant = createVariant(demoDebugVariantName)
        val variants = listOf(mainVariant, demoVariant, demoDebugVariant)
        every { variantTree.getVariants() }.returns(variants)

        templatesDirHandler.createResDirs()

        verify {
            sourceSetsHandler.addToSourceSets(getExpectedResFolderForVariant(mainVariantName), mainVariantName)
            sourceSetsHandler.addToSourceSets(getExpectedResFolderForVariant(demoVariantName), demoVariantName)
        }
        verify(exactly = 0) {
            sourceSetsHandler.addToSourceSets(
                getExpectedResFolderForVariant(demoDebugVariantName),
                demoDebugVariantName
            )
        }
    }

    @Test
    fun `Get list of created res dirs`() {
        val mainVariantName = "main"
        val demoVariantName = "demo"
        val demoDebugVariantName = "demoDebug"
        val mainVariant = createVariant(mainVariantName)
        val demoVariant = createVariant(demoVariantName)
        val demoDebugVariant = createVariant(demoDebugVariantName)
        val variants = listOf(mainVariant, demoVariant, demoDebugVariant)
        every { variantTree.getVariants() }.returns(variants)
        templatesDirHandler.createResDirs()

        val templatesDirs = templatesDirHandler.getTemplatesDirs()

        Truth.assertThat(templatesDirs).containsExactly(
            getExpectedResFolderForVariant(mainVariantName),
            getExpectedResFolderForVariant(demoVariantName)
        )
    }

    private fun getExpectedResFolderForVariant(variantName: String): File {
        return File(projectDir, "src/$variantName/templates")
    }

    private fun createVariant(name: String): Variant {
        val variant = mockk<Variant>()
        every { variant.name }.returns(name)

        return variant
    }
}