package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.OutputStringFileResolver
import com.likethesalad.placeholder.models.ResDirs
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class AndroidFilesProviderTest {

    private lateinit var outputStringFileResolver: OutputStringFileResolver
    private lateinit var incrementalDirsProvider: IncrementalDirsProvider
    private lateinit var androidFilesProvider: AndroidFilesProvider
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    companion object {
        private const val INCREMENTAL_FOLDER_NAME = "incremental"
    }

    @Before
    fun setUp() {
        outputStringFileResolver = mockk()
        incrementalDirsProvider = mockk()
        androidFilesProvider = AndroidFilesProvider(outputStringFileResolver, incrementalDirsProvider)
    }

//    @Test
//    fun check_getResolvedFileForValuesFolder_with_flavor() {
//        // Given:
//        setUpResDirs(flavorDirName = "demo")
//
//        // When:
//        val result = androidFilesProvider.getResolvedFile("")
//
//        // Then:
//        Truth.assertThat(result.absolutePath).endsWith("demo/res/values/resolved.xml")
//    }
//
//    @Test
//    fun check_getResolvedFileForValuesFolder_without_flavor() {
//        // Given:
//        setUpResDirs()
//
//        // When:
//        val result = androidFilesProvider.getResolvedFile("")
//
//        // Then:
//        Truth.assertThat(result.absolutePath).endsWith("main/res/values/resolved.xml")
//    }
//
//    @Test
//    fun check_getResolvedFileForValuesFolder_for_language_with_flavor() {
//        // Given:
//        setUpResDirs(flavorDirName = "demo")todo
//
//        // When:
//        val result = androidFilesProvider.getResolvedFile("-es")
//
//        // Then:
//        Truth.assertThat(result.absolutePath).endsWith("demo/res/values-es/resolved.xml")
//    }

//    @Test
//    fun check_getResolvedFileForValuesFolder_for_language_without_flavor() {
//        // Given:
//        setUpResDirs()
//
//        // When:
//        val result = androidFilesProvider.getResolvedFile("-es")
//
//        // Then:
//        Truth.assertThat(result.absolutePath).endsWith("main/res/values-es/resolved.xml")
//    }

    @Test
    fun check_getAllExpectedResolvedFiles() {
        // Given:
        setUpResDirs()
        val templateFiles = listOf("templates.json", "templates-es.json", "templates-it.json")
        createIncrementalFolder()
        createFolderInsideIncrementalFolder("templates")
        for (it in templateFiles) {
            temporaryFolder.newFile("$INCREMENTAL_FOLDER_NAME/templates/$it")
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
    fun check_getAllGatheredStringsFiles() {
        // Given:
        val stringFiles = setOf("strings.json", "strings-es.json", "strings-it.json")
        createIncrementalFolder()
        createFolderInsideIncrementalFolder("strings")
        for (it in stringFiles) {
            temporaryFolder.newFile("$INCREMENTAL_FOLDER_NAME/strings/$it")
        }

        // When:
        val result = androidFilesProvider.getAllGatheredStringsFiles()

        // Then:
        Truth.assertThat(result.size).isEqualTo(3)
        Truth.assertThat(result.map { it.name }).containsExactlyElementsIn(stringFiles)
    }

    @Test
    fun check_getAllGatheredStringsFiles_empty() {
        // Given:
        createIncrementalFolder()
        createFolderInsideIncrementalFolder("strings")

        // When:
        val result = androidFilesProvider.getAllGatheredStringsFiles()

        // Then:
        Truth.assertThat(result).isEmpty()
    }

    @Test
    fun check_getAllTemplatesFiles() {
        // Given:
        val templates = setOf("templates.json", "templates-es.json", "templates-it.json")
        createIncrementalFolder()
        createFolderInsideIncrementalFolder("templates")
        for (it in templates) {
            temporaryFolder.newFile("$INCREMENTAL_FOLDER_NAME/templates/$it")
        }

        // When:
        val result = androidFilesProvider.getAllTemplatesFiles()

        // Then:
        Truth.assertThat(result.map { it.name }).containsExactlyElementsIn(templates)
    }

    @Test
    fun check_getAllTemplatesFiles_empty() {
        // Given:
        createIncrementalFolder()
        createFolderInsideIncrementalFolder("templates")

        // When:
        val result = androidFilesProvider.getAllTemplatesFiles()

        // Then:
        Truth.assertThat(result).isEmpty()
    }

    @Test
    fun check_getAllExpectedTemplatesFiles() {
        // Given:
        val stringFiles = setOf("strings.json", "strings-es.json", "strings-it.json")
        createIncrementalFolder()
        createFolderInsideIncrementalFolder("strings")
        val templatesFolder = createFolderInsideIncrementalFolder("templates")
        for (it in stringFiles) {
            temporaryFolder.newFile("$INCREMENTAL_FOLDER_NAME/strings/$it")
        }

        // When:
        val result = androidFilesProvider.getAllExpectedTemplatesFiles()

        // Then:
        val templatesFolderPath = templatesFolder.absolutePath
        Truth.assertThat(result.map { it.absolutePath }).containsExactly(
            "$templatesFolderPath/templates.json",
            "$templatesFolderPath/templates-es.json",
            "$templatesFolderPath/templates-it.json"
        )
    }

    @Test
    fun check_getAllExpectedTemplatesFiles_empty() {
        // Given:
        createIncrementalFolder()
        createFolderInsideIncrementalFolder("strings")

        // When:
        val result = androidFilesProvider.getAllExpectedTemplatesFiles()

        // Then:
        Truth.assertThat(result).isEmpty()
    }

    private fun createIncrementalFolder(): File {
        val incrementalTestFolder = temporaryFolder.newFolder(INCREMENTAL_FOLDER_NAME)
        every { androidVariantHelper.incrementalDir }.returns(incrementalTestFolder.absolutePath)
        return incrementalTestFolder
    }

    private fun createFolderInsideIncrementalFolder(folderName: String): File {
        return temporaryFolder.newFolder(INCREMENTAL_FOLDER_NAME, folderName)
    }

    private fun setUpResDirs(
        mainResDirsNames: Set<String> = setOf("res"),
        flavorResDirsNames: Set<String> = setOf("res"),
        flavorDirName: String? = null
    ): ResDirs {
        val mainDirName = "main"
        val mainResDirs = mutableSetOf<File>()
        for (it in mainResDirsNames) {
            mainResDirs.add(temporaryFolder.newFolder(mainDirName, it))
        }

        val flavorResDirs = mutableSetOf<File>()
        if (flavorDirName != null) {
            for (it in flavorResDirsNames) {
                flavorResDirs.add(temporaryFolder.newFolder(flavorDirName, it))
            }
        }
        val resourceDirs = ResDirs(mainResDirs, flavorResDirs)
//        every { androidVariantHelper.resourceDirs }.returns(resourceDirs)todo
        return resourceDirs
    }
}