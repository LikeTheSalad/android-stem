package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.google.common.truth.Truth
import com.likethesalad.placeholder.providers.ProjectDirsProvider
import com.likethesalad.tools.resource.api.android.environment.Variant
import com.likethesalad.tools.resource.collector.android.data.resdir.ResDir
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.junit.Assert.fail
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
            sourceSetsHandler.addToSourceSets(
                getExpectedResFolderForVariant(demoDebugVariantName),
                demoDebugVariantName
            )
        }
    }

    @Test
    fun `Validate creation of dirs is done only once`() {
        val mainVariantName = "main"
        val demoVariantName = "demo"
        val mainVariant = createVariant(mainVariantName)
        val demoVariant = createVariant(demoVariantName)
        val variants = listOf(mainVariant, demoVariant)
        every { variantTree.getVariants() }.returns(variants)
        templatesDirHandler.createResDirs()

        try {
            templatesDirHandler.createResDirs()
            fail()
        } catch (e: IllegalStateException) {
            Truth.assertThat(e.message).isEqualTo("Res dirs have already been created")
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
            ResDir(mainVariant, getExpectedResFolderForVariant(mainVariantName)),
            ResDir(demoVariant, getExpectedResFolderForVariant(demoVariantName)),
            ResDir(demoDebugVariant, getExpectedResFolderForVariant(demoDebugVariantName))
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