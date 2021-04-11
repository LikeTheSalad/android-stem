package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.common.helpers.dirs.IncrementalDirsProvider
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
    private lateinit var androidVariantContext: AndroidVariantContext
    private lateinit var dirsProvider: IncrementalDirsProvider

    @Before
    fun setup() {
        incrementalDir = temporaryFolder.newFolder("incremental")
        androidVariantContext = mockk()
        every { androidVariantContext.incrementalDir }.returns(incrementalDir.absolutePath)

        dirsProvider =
            IncrementalDirsProvider(
                androidVariantContext
            )
    }

    @Test
    fun `Get raw strings incremental dir and create it if it doesn't exist`() {
        val result = dirsProvider.getRawStringsDir()

        Truth.assertThat(result.exists()).isTrue()
        Truth.assertThat(result.absolutePath).isEqualTo(
            "${incrementalDir.absolutePath}/strings"
        )
    }

    @Test
    fun `Get template strings incremental dir and create it if it doesn't exist`() {
        val result = dirsProvider.getTemplateStringsDir()

        Truth.assertThat(result.exists()).isTrue()
        Truth.assertThat(result.absolutePath).isEqualTo(
            "${incrementalDir.absolutePath}/templates"
        )
    }
}