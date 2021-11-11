package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.dirs.IncrementalDirsProvider
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantBuildResolvedDir
import com.likethesalad.placeholder.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.tools.resource.api.android.environment.Language
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
            Language.Default,
            "clientDebug/res/values/resolved.xml"
        )
        assertResolvedStringsFilePath(
            Language.Custom("es"),
            "clientDebug/res/values-es/resolved.xml"
        )
    }

    @Test
    fun `Get resolved strings file when there's more than a res folder`() {
        every { variantBuildResolvedDir.resolvedDir }.returns(File(srcDir, "clientDebug/res1/"))

        assertResolvedStringsFilePath(
            Language.Default,
            "clientDebug/res1/values/resolved.xml"
        )
        assertResolvedStringsFilePath(
            Language.Custom("es"),
            "clientDebug/res1/values-es/resolved.xml"
        )
    }

    @Test
    fun `Get raw gathered strings file`() {
        assertRawStringsFilePath(
            Language.Default,
            "strings/strings.json"
        )
        assertRawStringsFilePath(
            Language.Custom("es"),
            "strings/strings-es.json"
        )
    }

    @Test
    fun `Get template strings file`() {
        assertTemplateStringsFilePath(
            Language.Default,
            "templates/templates.json"
        )
        assertTemplateStringsFilePath(
            Language.Custom("es"),
            "templates/templates-es.json"
        )
    }

    private fun assertTemplateStringsFilePath(language: Language, expectedRelativePath: String) {
        Truth.assertThat(outputStringFileResolver.getTemplateStringsFile(language.id).absolutePath)
            .isEqualTo(File(incrementalDir, expectedRelativePath).absolutePath)
    }

    private fun assertRawStringsFilePath(language: Language, expectedRelativePath: String) {
        Truth.assertThat(outputStringFileResolver.getRawStringsFile(language.id).absolutePath)
            .isEqualTo(File(incrementalDir, expectedRelativePath).absolutePath)
    }

    private fun assertResolvedStringsFilePath(language: Language, expectedRelativePath: String) {
        Truth.assertThat(
            outputStringFileResolver.getResolvedStringsFile(language.id)
                .absolutePath
        ).isEqualTo(File(srcDir, expectedRelativePath).absolutePath)
    }
}