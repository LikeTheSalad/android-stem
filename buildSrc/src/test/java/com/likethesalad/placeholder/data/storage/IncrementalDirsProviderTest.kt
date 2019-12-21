package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.AndroidVariantHelper
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class IncrementalDirsProviderTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var incrementalDir: File
    private lateinit var androidVariantHelper: AndroidVariantHelper
    private lateinit var dirsProvider: IncrementalDirsProvider

    @Before
    fun setup() {
        incrementalDir = temporaryFolder.newFolder("incremental")
        androidVariantHelper = mockk()
        every { androidVariantHelper.incrementalDir }.returns(incrementalDir.absolutePath)

        dirsProvider = IncrementalDirsProvider(androidVariantHelper)
    }

    @Test
    fun `Get raw strings incremental dir`() {
        Truth.assertThat(dirsProvider.getRawStringsDir().absolutePath).isEqualTo(
            "${incrementalDir.absolutePath}/strings"
        )
    }

    @Test
    fun `Get template strings incremental dir`() {
        Truth.assertThat(dirsProvider.getTemplateStringsDir().absolutePath).isEqualTo(
            "${incrementalDir.absolutePath}/templates"
        )
    }
}