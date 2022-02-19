package com.likethesalad.stem.data

import com.google.common.truth.Truth
import com.likethesalad.stem.modules.common.helpers.files.OutputStringFileResolver
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
    private lateinit var outputStringFileResolver: OutputStringFileResolver

    @Before
    fun setup() {
        srcDir = temporaryFolder.newFolder(srcDirName)
        incrementalDir = temporaryFolder.newFolder("build", "incremental", "taskName")
        outputStringFileResolver = OutputStringFileResolver()
    }

    @Test
    fun `Get resolved strings file`() {
        val resolvedDir = File(srcDir, "clientDebug/res/")

        assertResolvedStringsFilePath(
            resolvedDir,
            "values",
            "clientDebug/res/values/resolved.xml"
        )
        assertResolvedStringsFilePath(
            resolvedDir,
            "values-es",
            "clientDebug/res/values-es/resolved.xml"
        )
    }

    @Test
    fun `Get resolved strings file when there's more than a res folder`() {
        val resolvedDir = File(srcDir, "clientDebug/res1/")

        assertResolvedStringsFilePath(
            resolvedDir,
            "values",
            "clientDebug/res1/values/resolved.xml"
        )
        assertResolvedStringsFilePath(
            resolvedDir,
            "values-es",
            "clientDebug/res1/values-es/resolved.xml"
        )
    }

    @Test
    fun `Get template strings file`() {
        val templatesDir = File(incrementalDir, "templates")
        assertTemplateStringsFilePath(
            templatesDir,
            "",
            "templates/templates.json"
        )
        assertTemplateStringsFilePath(
            templatesDir,
            "-es",
            "templates/templates-es.json"
        )
    }

    private fun assertTemplateStringsFilePath(templatesDir: File, suffix: String, expectedRelativePath: String) {
        Truth.assertThat(outputStringFileResolver.getTemplateStringsFile(templatesDir, suffix).absolutePath)
            .isEqualTo(File(incrementalDir, expectedRelativePath).absolutePath)
    }

    private fun assertResolvedStringsFilePath(
        resolvedDir: File,
        valuesFolderName: String,
        expectedRelativePath: String
    ) {
        Truth.assertThat(
            outputStringFileResolver.getResolvedStringsFile(resolvedDir, valuesFolderName)
                .absolutePath
        ).isEqualTo(File(srcDir, expectedRelativePath).absolutePath)
    }
}