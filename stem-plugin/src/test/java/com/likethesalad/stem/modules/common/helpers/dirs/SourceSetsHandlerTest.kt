package com.likethesalad.stem.modules.common.helpers.dirs

import com.likethesalad.stem.providers.AndroidExtensionProvider
import com.likethesalad.tools.agpcompat.api.bridges.AndroidExtension
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

class SourceSetsHandlerTest : BaseMockable() {

    @MockK
    lateinit var androidExtensionProvider: AndroidExtensionProvider

    @MockK
    lateinit var androidExtension: AndroidExtension

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var sourceSetsHandler: SourceSetsHandler

    @Before
    fun setUp() {
        sourceSetsHandler = SourceSetsHandler(androidExtensionProvider)
        every { androidExtensionProvider.getExtension() }.returns(androidExtension)
    }

    @Test
    fun `Add dir to variant sourceSets`() {
        val variantName = "someVariant"
        val newSrcDir = temporaryFolder.newFolder("newRes")
        every { androidExtension.addVariantSrcDir(any(), any()) } just Runs

        sourceSetsHandler.addToSourceSets(newSrcDir, variantName)

        verify {
            androidExtension.addVariantSrcDir(
                variantName,
                newSrcDir
            )
        }
    }
}