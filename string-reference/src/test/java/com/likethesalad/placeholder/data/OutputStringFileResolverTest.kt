package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.dirs.IncrementalDirsProvider
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantBuildResolvedDir
import com.likethesalad.placeholder.modules.common.helpers.files.OutputStringFileResolver
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
    private lateinit var incrementalDirsProvider: IncrementalDirsProvider
    private lateinit var variantBuildResolvedDir: VariantBuildResolvedDir
    private lateinit var outputStringFileResolver: OutputStringFileResolver

    @Before
    fun setup() {
        srcDir = temporaryFolder.newFolder(srcDirName)
        incrementalDir = temporaryFolder.newFolder("build", "incremental", "taskName")
        incrementalDirsProvider =
            IncrementalDirsProvider(
                incrementalDir
            )
        variantBuildResolvedDir = mockk()
        outputStringFileResolver =
            OutputStringFileResolver(
                variantBuildResolvedDir,
                incrementalDirsProvider
            )
    }

    @Test
    fun `Get resolved strings file`() {
        every { variantBuildResolvedDir.resolvedDir }.returns(File(srcDir, "clientDebug/res/"))

        assertResolvedStringsFilePath(
            "values",
            "clientDebug/res/values/resolved.xml"
        )
        assertResolvedStringsFilePath(
            "values-es",
            "clientDebug/res/values-es/resolved.xml"
        )
    }

    @Test
    fun `Get resolved strings file when there's more than a res folder`() {
        every { variantBuildResolvedDir.resolvedDir }.returns(File(srcDir, "clientDebug/res1/"))

        assertResolvedStringsFilePath(
            "values",
            "clientDebug/res1/values/resolved.xml"
        )
        assertResolvedStringsFilePath(
            "values-es",
            "clientDebug/res1/values-es/resolved.xml"
        )
    }

    @Test
    fun `Get template strings file`() {
        assertTemplateStringsFilePath(
            "",
            "templates/templates.json"
        )
        assertTemplateStringsFilePath(
            "-es",
            "templates/templates-es.json"
        )
    }

    private fun assertTemplateStringsFilePath(suffix: String, expectedRelativePath: String) {
        Truth.assertThat(outputStringFileResolver.getTemplateStringsFile(suffix).absolutePath)
            .isEqualTo(File(incrementalDir, expectedRelativePath).absolutePath)
    }

    private fun assertResolvedStringsFilePath(valuesFolderName: String, expectedRelativePath: String) {
        Truth.assertThat(
            outputStringFileResolver.getResolvedStringsFile(valuesFolderName)
                .absolutePath
        ).isEqualTo(File(srcDir, expectedRelativePath).absolutePath)
    }
}