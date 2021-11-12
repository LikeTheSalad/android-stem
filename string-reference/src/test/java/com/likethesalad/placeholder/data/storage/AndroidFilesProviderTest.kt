package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.dirs.IncrementalDirsProvider
import com.likethesalad.placeholder.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.placeholder.modules.common.helpers.files.storage.AndroidFilesProvider
import com.likethesalad.placeholder.providers.LanguageResourceFinderProvider
import com.likethesalad.tools.resource.locator.android.extension.LanguageResourceFinder
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class AndroidFilesProviderTest {

    private lateinit var outputStringFileResolver: OutputStringFileResolver
    private lateinit var incrementalDirsProvider: IncrementalDirsProvider
    private lateinit var androidFilesProvider: AndroidFilesProvider
    private lateinit var languageResourceFinderProvider: LanguageResourceFinderProvider
    private lateinit var languageResourceFinder: LanguageResourceFinder

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var templatesDir: File

    companion object {
        private const val INCREMENTAL_FOLDER_NAME = "incremental"
        private const val TEMPLATES_FOLDER_NAME = "templates"
        private const val MERGED_STRINGS_FOLDER_NAME = "strings"
    }

    @Before
    fun setUp() {
        outputStringFileResolver = mockk()
        incrementalDirsProvider = mockk()
        languageResourceFinderProvider = mockk()
        languageResourceFinder = mockk()
        templatesDir = temporaryFolder.newFolder(INCREMENTAL_FOLDER_NAME, TEMPLATES_FOLDER_NAME)
        every { incrementalDirsProvider.getTemplateStringsDir() }.returns(templatesDir)
        every { languageResourceFinderProvider.get() }.returns(languageResourceFinder)
        androidFilesProvider =
            AndroidFilesProvider(
                outputStringFileResolver,
                incrementalDirsProvider,
                languageResourceFinderProvider
            )
    }

    @Test
    fun check_getAllExpectedResolvedFiles() {
        // Given:
        val templateFiles = listOf("templates.json", "templates-es.json", "templates-it.json")
        addTemplateFilesToTemplatesDir(templateFiles)
        val valuesFolderName = slot<String>()
        every {
            outputStringFileResolver.getResolvedStringsFile(capture(valuesFolderName))
        }.answers {
            File(temporaryFolder.root, "main/res/${valuesFolderName.captured}/resolved.xml")
        }

        // When:
        val result = androidFilesProvider.getAllExpectedResolvedFiles()

        // Then:

        Truth.assertThat(result.map { it.absolutePath.replace(temporaryFolder.root.absolutePath, "") })
            .containsExactly(
                "/main/res/values/resolved.xml",
                "/main/res/values-es/resolved.xml",
                "/main/res/values-it/resolved.xml"
            )
    }

    @Test
    fun check_getAllTemplatesFiles() {
        // Given:
        val templates = listOf("templates.json", "templates-es.json", "templates-it.json")
        addTemplateFilesToTemplatesDir(templates)

        // When:
        val result = androidFilesProvider.getAllTemplatesFiles()

        // Then:
        Truth.assertThat(result.map { it.name }).containsExactlyElementsIn(templates)
    }

    @Test
    fun check_getAllTemplatesFiles_empty() {
        // When:
        val result = androidFilesProvider.getAllTemplatesFiles()

        // Then:
        Truth.assertThat(result).isEmpty()
    }

    @Test
    fun check_getAllExpectedTemplatesFiles() {
        // Given:
        val stringFiles = listOf("strings.json", "strings-es.json", "strings-it.json")
        addStringFilesToMergedStringsDir(stringFiles)
        val suffix = slot<String>()
        every {
            outputStringFileResolver.getTemplateStringsFile(capture(suffix))
        }.answers {
            File(templatesDir, "templates${suffix.captured}.json")
        }

        // When:
        val result = androidFilesProvider.getAllExpectedTemplatesFiles()

        // Then:
        val templatesFolderPath = templatesDir.absolutePath
        Truth.assertThat(result.map { it.absolutePath }).containsExactly(
            "$templatesFolderPath/templates.json",
            "$templatesFolderPath/templates-es.json",
            "$templatesFolderPath/templates-it.json"
        )
    }

    @Test
    fun check_getAllExpectedTemplatesFiles_empty() {
        // When:
        val result = androidFilesProvider.getAllExpectedTemplatesFiles()

        // Then:
        Truth.assertThat(result).isEmpty()
    }

    private fun addTemplateFilesToTemplatesDir(templateFileNames: List<String>) {
        addFilesToDir(templateFileNames, "$INCREMENTAL_FOLDER_NAME/$TEMPLATES_FOLDER_NAME")
    }

    private fun addStringFilesToMergedStringsDir(stringFileNames: List<String>) {
        addFilesToDir(stringFileNames, "$INCREMENTAL_FOLDER_NAME/$MERGED_STRINGS_FOLDER_NAME")
    }

    private fun addFilesToDir(fileNames: List<String>, dirPath: String) {
        for (fileName in fileNames) {
            temporaryFolder.newFile("$dirPath/$fileName")
        }
    }
}