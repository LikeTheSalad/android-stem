package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.AndroidVariantHelper
import com.likethesalad.placeholder.data.storage.IncrementalDirsProvider
import com.likethesalad.placeholder.data.storage.VariantBuildResolvedDir
import com.likethesalad.placeholder.models.PathIdentity
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class OutputStringFileResolverTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val srcDirName = "src"
    private lateinit var srcDir: File
    private lateinit var incrementalDir: File
    private lateinit var androidVariantHelper: AndroidVariantHelper
    private lateinit var incrementalDirsProvider: IncrementalDirsProvider
    private lateinit var variantBuildResolvedDir: VariantBuildResolvedDir
    private lateinit var outputStringFileResolver: OutputStringFileResolver

    @Before
    fun setup() {
        srcDir = temporaryFolder.newFolder(srcDirName)
        incrementalDir = temporaryFolder.newFolder("build", "incremental", "taskName")
        androidVariantHelper = mockk()
        every { androidVariantHelper.incrementalDir }.returns(incrementalDir.absolutePath)
        incrementalDirsProvider = IncrementalDirsProvider(androidVariantHelper)
        variantBuildResolvedDir = mockk()
        outputStringFileResolver = OutputStringFileResolver(
            variantBuildResolvedDir,
            incrementalDirsProvider
        )
    }

    @Test
    fun `Get resolved strings file`() {
        every { variantBuildResolvedDir.resolvedDir }.returns(File(srcDir, "clientDebug/res/"))

        assertResolvedStringsFilePath(
            PathIdentity("values", ""),
            "clientDebug/res/values/resolved.xml"
        )
        assertResolvedStringsFilePath(
            PathIdentity("values-es", "-es"),
            "clientDebug/res/values-es/resolved.xml"
        )
    }

    @Test
    fun `Get resolved strings file when there's more than a res folder`() {
        every { variantBuildResolvedDir.resolvedDir }.returns(File(srcDir, "clientDebug/res1/"))

        assertResolvedStringsFilePath(
            PathIdentity("values", ""),
            "clientDebug/res1/values/resolved.xml"
        )
        assertResolvedStringsFilePath(
            PathIdentity("values-es", "-es"),
            "clientDebug/res1/values-es/resolved.xml"
        )
    }

    @Test
    fun `Get raw gathered strings file`() {
        assertRawStringsFilePath(
            PathIdentity("values", ""),
            "strings/strings.json"
        )
        assertRawStringsFilePath(
            PathIdentity("values-es", "-es"),
            "strings/strings-es.json"
        )
    }

    @Test
    fun `Get template strings file`() {
        assertTemplateStringsFilePath(
            PathIdentity("values", ""),
            "templates/templates.json"
        )
        assertTemplateStringsFilePath(
            PathIdentity("values-es", "-es"),
            "templates/templates-es.json"
        )
    }

    private fun assertTemplateStringsFilePath(pathIdentity: PathIdentity, expectedRelativePath: String) {
        Truth.assertThat(outputStringFileResolver.getTemplateStringsFile(pathIdentity.suffix).absolutePath)
            .isEqualTo(File(incrementalDir, expectedRelativePath).absolutePath)
    }

    private fun assertRawStringsFilePath(pathIdentity: PathIdentity, expectedRelativePath: String) {
        Truth.assertThat(outputStringFileResolver.getRawStringsFile(pathIdentity.suffix).absolutePath)
            .isEqualTo(File(incrementalDir, expectedRelativePath).absolutePath)
    }

    private fun assertResolvedStringsFilePath(pathIdentity: PathIdentity, expectedRelativePath: String) {
        Truth.assertThat(
            outputStringFileResolver.getResolvedStringsFile(pathIdentity.valuesFolderName)
                .absolutePath
        ).isEqualTo(File(srcDir, expectedRelativePath).absolutePath)
    }
}