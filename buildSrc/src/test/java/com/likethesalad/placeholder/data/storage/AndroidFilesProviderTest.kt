package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.AndroidVariantHelper
import com.likethesalad.placeholder.models.ResDirs
import com.likethesalad.placeholder.models.raw.FlavorValuesRawFiles
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class AndroidFilesProviderTest {

    private lateinit var androidFilesProvider: AndroidFilesProvider
    private lateinit var androidVariantHelper: AndroidVariantHelper
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    companion object {
        private const val INCREMENTAL_FOLDER_NAME = "incremental"
    }

    @Before
    fun setUp() {
        androidVariantHelper = mockk()
        androidFilesProvider = AndroidFilesProvider(androidVariantHelper)
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
    fun check_getAllExpectedResolvedFiles_without_flavor() {
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
    fun check_getAllExpectedResolvedFiles_with_flavor() {
        // Given:
        setUpResDirs(flavorDirName = "demo")
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
                "/demo/res/values/resolved.xml",
                "/demo/res/values-es/resolved.xml",
                "/demo/res/values-it/resolved.xml"
            )
    }

    @Test
    fun check_getAllExpectedResolvedFiles_with_flavor_empty_templates() {
        // Given:
        setUpResDirs(flavorDirName = "demo")
        createIncrementalFolder()
        createFolderInsideIncrementalFolder("templates")

        // When:
        val result = androidFilesProvider.getAllExpectedResolvedFiles()

        // Then:
        Truth.assertThat(result).isEmpty()
    }

    @Test
    fun check_getAllExpectedResolvedFiles_without_flavor_empty_templates() {
        // Given:
        setUpResDirs()
        createIncrementalFolder()
        createFolderInsideIncrementalFolder("templates")

        // When:
        val result = androidFilesProvider.getAllExpectedResolvedFiles()

        // Then:
        Truth.assertThat(result).isEmpty()
    }

    @Test
    fun check_getGatheredStringsFileForFolder() {
        // Given:
        val incrementalTestFolder = createIncrementalFolder()

        // When:
        val result = androidFilesProvider.getGatheredStringsFile()

        // Then:
        Truth.assertThat(result.absolutePath).isEqualTo("${incrementalTestFolder.absolutePath}/strings/strings.json")
    }

    @Test
    fun check_getGatheredStringsFileForFolder_language() {
        // Given:
        val incrementalTestFolder = createIncrementalFolder()

        // When:
        val result = androidFilesProvider.getGatheredStringsFile("-es")

        // Then:
        Truth.assertThat(result.absolutePath).isEqualTo("${incrementalTestFolder.absolutePath}/strings/strings-es.json")
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
    fun check_getTemplateFileForStringFile() {
        // Given:
        createIncrementalFolder()
        val templatesFolder = createFolderInsideIncrementalFolder("templates")

        // When:
        val result = androidFilesProvider.getTemplateFile()

        // Then:
        Truth.assertThat(result.absolutePath).isEqualTo("${templatesFolder.absolutePath}/templates.json")
    }

    @Test
    fun check_getTemplateFileForStringFile_language() {
        // Given:
        createIncrementalFolder()
        val templatesFolder = createFolderInsideIncrementalFolder("templates")

        // When:
        val result = androidFilesProvider.getTemplateFile("-es")

        // Then:
        Truth.assertThat(result.absolutePath).isEqualTo("${templatesFolder.absolutePath}/templates-es.json")
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

    @Test
    fun check_getRawResourcesFilesForFolder_without_flavor() {
        // Given:
        val valuesFolderName = "values"
        setUpResDirs()
        val valuesRawFileNames = setOf(
            "strings.xml",
            "config.xml",
            "else.json"
        )
        temporaryFolder.newFolder("main", "res", valuesFolderName)
        for (it in valuesRawFileNames) {
            temporaryFolder.newFile("main/res/$valuesFolderName/$it")
        }
        every { androidVariantHelper.isFlavored }.returns(false)

        // When:
        val result = androidFilesProvider.getRawResourcesFilesForFolder(valuesFolderName)

        // Then:
        Truth.assertThat(result.suffix).isEmpty()
        Truth.assertThat(result.mainValuesRawFiles.map { it.name }).containsExactly(
            "strings.xml",
            "config.xml"
        )
    }

    @Test
    fun check_getRawResourcesFilesForFolder_with_flavor() {
        // Given:
        val valuesFolderName = "values"
        setUpResDirs(flavorDirName = "demo")
        val valuesRawFileNames = setOf(
            "strings.xml",
            "config.xml",
            "else.json"
        )
        temporaryFolder.newFolder("demo", "res", valuesFolderName)
        for (it in valuesRawFileNames) {
            temporaryFolder.newFile("demo/res/$valuesFolderName/$it")
        }
        every { androidVariantHelper.isFlavored }.returns(true)
        every { androidVariantHelper.flavor }.returns("demo")

        // When:
        val result = androidFilesProvider.getRawResourcesFilesForFolder(valuesFolderName)

        // Then:
        Truth.assertThat(result.suffix).isEmpty()
        Truth.assertThat(result.mainValuesRawFiles.map { it.name }).containsExactly(
            "strings.xml",
            "config.xml"
        )
    }

    @Test
    fun check_getRawResourcesFilesForFolder_without_flavor_ignore_resolved() {
        // Given:
        val valuesFolderName = "values"
        setUpResDirs()
        val valuesRawFileNames = setOf(
            "strings.xml",
            "config.xml",
            "resolved.xml",
            "else.json",
            "resolved2.xml"
        )
        temporaryFolder.newFolder("main", "res", valuesFolderName)
        for (it in valuesRawFileNames) {
            temporaryFolder.newFile("main/res/$valuesFolderName/$it")
        }
        every { androidVariantHelper.isFlavored }.returns(false)

        // When:
        val result = androidFilesProvider.getRawResourcesFilesForFolder(valuesFolderName)

        // Then:
        Truth.assertThat(result.suffix).isEmpty()
        Truth.assertThat(result.mainValuesRawFiles.map { it.name }).containsExactly(
            "strings.xml",
            "config.xml",
            "resolved2.xml"
        )
    }

    @Test
    fun check_getRawResourcesFilesForFolder_with_flavor_ignore_resolved() {
        // Given:
        val valuesFolderName = "values"
        setUpResDirs(flavorDirName = "demo")
        val valuesRawFileNames = setOf(
            "strings.xml",
            "config.xml",
            "else.json",
            "else2.json",
            "resolved.xml",
            "resolved2.xml",
            "the_resolved.xml"
        )
        temporaryFolder.newFolder("demo", "res", valuesFolderName)
        for (it in valuesRawFileNames) {
            temporaryFolder.newFile("demo/res/$valuesFolderName/$it")
        }
        every { androidVariantHelper.isFlavored }.returns(true)
        every { androidVariantHelper.flavor }.returns("demo")

        // When:
        val result = androidFilesProvider.getRawResourcesFilesForFolder(valuesFolderName)

        // Then:
        Truth.assertThat(result.suffix).isEmpty()
        Truth.assertThat(result.mainValuesRawFiles.map { it.name }).containsExactly(
            "strings.xml",
            "config.xml",
            "resolved2.xml",
            "the_resolved.xml"
        )
    }

    @Test
    fun check_getAllFoldersRawResourcesFiles_without_flavor() {
        // Given:
        val valuesFolder = "values"
        val xmlFiles = setOf(
            "strings.xml",
            "something.xml",
            "else.xml"
        )
        setUpResDirs()
        temporaryFolder.newFolder("main", "res", valuesFolder)
        for (it in xmlFiles) {
            temporaryFolder.newFile("main/res/$valuesFolder/$it")
        }
        every { androidVariantHelper.isFlavored }.returns(false)

        // When:
        val result = androidFilesProvider.getAllFoldersRawResourcesFiles()

        // Then:
        Truth.assertThat(result.size).isEqualTo(1)
        val first = result.first()
        Truth.assertThat(first.mainValuesRawFiles.map { it.name }).containsExactlyElementsIn(xmlFiles)
    }

    @Test
    fun check_getAllFoldersRawResourcesFiles_without_flavor_many_res() {
        // Given:
        val valuesFolder = "values"
        val xmlFilesRes = setOf(
            "strings.xml",
            "something.xml",
            "else.xml"
        )
        val xmlFilesRes2 = setOf(
            "strings.xml",
            "the_other.xml",
            "else2.xml",
            "config.xml"
        )
        setUpResDirs(mainResDirsNames = setOf("res", "res2"))
        // Setup res:
        val resValuesFolder = temporaryFolder.newFolder("main", "res", valuesFolder)
        for (it in xmlFilesRes) {
            temporaryFolder.newFile("main/res/$valuesFolder/$it")
        }
        // Setup res2:
        val res2ValuesFolder = temporaryFolder.newFolder("main", "res2", valuesFolder)
        for (it in xmlFilesRes2) {
            temporaryFolder.newFile("main/res2/$valuesFolder/$it")
        }
        every { androidVariantHelper.isFlavored }.returns(false)

        // When:
        val result = androidFilesProvider.getAllFoldersRawResourcesFiles()

        // Then:
        Truth.assertThat(result.size).isEqualTo(1)
        val first = result.first()
        Truth.assertThat(first.mainValuesRawFiles.map { it.absolutePath })
            .containsExactly(
                "${resValuesFolder.absolutePath}/strings.xml",
                "${resValuesFolder.absolutePath}/something.xml",
                "${resValuesFolder.absolutePath}/else.xml",
                "${res2ValuesFolder.absolutePath}/strings.xml",
                "${res2ValuesFolder.absolutePath}/the_other.xml",
                "${res2ValuesFolder.absolutePath}/else2.xml",
                "${res2ValuesFolder.absolutePath}/config.xml"
            )
    }

    @Test
    fun check_getAllFoldersRawResourcesFiles_without_flavor_languages() {
        // Given:
        val valuesFolder = "values"
        val langValuesFolder = "values-es"
        val xmlFilesRes = setOf(
            "strings.xml",
            "something.xml",
            "config.xml",
            "else.xml"
        )
        val xmlFilesResEs = setOf(
            "strings.xml",
            "something.xml",
            "else.xml"
        )
        setUpResDirs()
        // Setup res:
        val resValuesFolder = temporaryFolder.newFolder("main", "res", valuesFolder)
        for (it in xmlFilesRes) {
            temporaryFolder.newFile("main/res/$valuesFolder/$it")
        }
        // Setup resEs:
        val resEsValuesFolder = temporaryFolder.newFolder("main", "res", langValuesFolder)
        for (it in xmlFilesResEs) {
            temporaryFolder.newFile("main/res/$langValuesFolder/$it")
        }
        every { androidVariantHelper.isFlavored }.returns(false)

        // When:
        val result = androidFilesProvider.getAllFoldersRawResourcesFiles()

        // Then:
        Truth.assertThat(result.size).isEqualTo(2)
        val first = result.first()
        Truth.assertThat(first.mainValuesRawFiles.map { it.absolutePath })
            .containsExactly(
                "${resValuesFolder.absolutePath}/strings.xml",
                "${resValuesFolder.absolutePath}/something.xml",
                "${resValuesFolder.absolutePath}/else.xml",
                "${resValuesFolder.absolutePath}/config.xml"
            )
        val second = result[1]
        Truth.assertThat(second.mainValuesRawFiles.map { it.absolutePath })
            .containsExactly(
                "${resEsValuesFolder.absolutePath}/strings.xml",
                "${resEsValuesFolder.absolutePath}/something.xml",
                "${resEsValuesFolder.absolutePath}/else.xml"
            )
    }

    @Test
    fun check_getAllFoldersRawResourcesFiles_without_flavor_languages_many_res() {
        // Given:
        val valuesFolder = "values"
        val langValuesFolder = "values-es"
        val xmlFilesRes = setOf(
            "strings.xml",
            "something.xml",
            "config.xml",
            "else.xml"
        )
        val xmlFilesResEs = setOf(
            "strings.xml",
            "something.xml",
            "else.xml"
        )
        setUpResDirs(mainResDirsNames = setOf("res", "res2"))
        // Setup res:
        val resValuesFolder = temporaryFolder.newFolder("main", "res", valuesFolder)
        for (it in xmlFilesRes) {
            temporaryFolder.newFile("main/res/$valuesFolder/$it")
        }
        // Setup resEs:
        val resEsValuesFolder = temporaryFolder.newFolder("main", "res2", langValuesFolder)
        for (it in xmlFilesResEs) {
            temporaryFolder.newFile("main/res2/$langValuesFolder/$it")
        }
        every { androidVariantHelper.isFlavored }.returns(false)

        // When:
        val result = androidFilesProvider.getAllFoldersRawResourcesFiles()

        // Then:
        Truth.assertThat(result.size).isEqualTo(2)
        val first = result.first()
        Truth.assertThat(first.mainValuesRawFiles.map { it.absolutePath })
            .containsExactly(
                "${resValuesFolder.absolutePath}/strings.xml",
                "${resValuesFolder.absolutePath}/something.xml",
                "${resValuesFolder.absolutePath}/else.xml",
                "${resValuesFolder.absolutePath}/config.xml"
            )
        val second = result[1]
        Truth.assertThat(second.mainValuesRawFiles.map { it.absolutePath })
            .containsExactly(
                "${resEsValuesFolder.absolutePath}/strings.xml",
                "${resEsValuesFolder.absolutePath}/something.xml",
                "${resEsValuesFolder.absolutePath}/else.xml"
            )
    }

    @Test
    fun check_getAllFoldersRawResourcesFiles_with_flavor() {
        // Given:
        val valuesFolder = "values"
        val xmlFilesMain = setOf(
            "strings.xml",
            "something.xml",
            "else.xml"
        )
        val xmlFilesFlavor = setOf(
            "strings.xml",
            "some_flavor_strings.xml"
        )
        setUpResDirs(flavorDirName = "demo")
        // Setup main
        val mainValuesFolder = temporaryFolder.newFolder("main", "res", valuesFolder)
        for (it in xmlFilesMain) {
            temporaryFolder.newFile("main/res/$valuesFolder/$it")
        }
        //Setup flavor
        val flavorValuesFolder = temporaryFolder.newFolder("demo", "res", valuesFolder)
        for (it in xmlFilesFlavor) {
            temporaryFolder.newFile("demo/res/$valuesFolder/$it")
        }
        every { androidVariantHelper.isFlavored }.returns(true)
        every { androidVariantHelper.flavor }.returns("demo")

        // When:
        val result = androidFilesProvider.getAllFoldersRawResourcesFiles()

        // Then:
        Truth.assertThat(result.size).isEqualTo(1)
        val first = result.first() as FlavorValuesRawFiles
        Truth.assertThat(first.mainValuesRawFiles.map { it.absolutePath }).containsExactly(
            "${flavorValuesFolder.absolutePath}/strings.xml",
            "${flavorValuesFolder.absolutePath}/some_flavor_strings.xml"
        )
        Truth.assertThat(first.complimentaryRawFiles.map { it.absolutePath }).containsExactly(
            "${mainValuesFolder.absolutePath}/strings.xml",
            "${mainValuesFolder.absolutePath}/something.xml",
            "${mainValuesFolder.absolutePath}/else.xml"
        )
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
        every { androidVariantHelper.resourceDirs }.returns(resourceDirs)
        return resourceDirs
    }
}