package com.likethesalad.placeholder.tasks.actions

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.resources.ResourcesHandler
import com.likethesalad.placeholder.data.storage.FilesProvider
import com.likethesalad.placeholder.models.StringsTemplatesModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class GatherTemplatesActionTest {

    private lateinit var gatherTemplatesAction: GatherTemplatesAction
    private lateinit var filesProvider: FilesProvider
    private lateinit var resourcesHandler: ResourcesHandler

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Before
    fun setUp() {
        filesProvider = mockk()
//        resourcesHandler = spyk(AndroidResourcesHandler(filesProvider))
        gatherTemplatesAction = GatherTemplatesAction(filesProvider, resourcesHandler)
    }

    @Test
    fun check_getStringFiles() {
        // Given:
        val files = listOf<File>(mockk())
        every { filesProvider.getAllGatheredStringsFiles() } returns files

        // When:
        val result = gatherTemplatesAction.getStringFiles()

        // Then:
        verify { filesProvider.getAllGatheredStringsFiles() }
        Truth.assertThat(result).isEqualTo(files)
    }

    @Test
    fun check_getTemplatesFiles() {
        // Given:
        val files = listOf<File>(mockk())
        every { filesProvider.getAllExpectedTemplatesFiles() } returns files

        // When:
        val result = gatherTemplatesAction.getTemplatesFiles()

        // Then:
        verify { filesProvider.getAllExpectedTemplatesFiles() }
        Truth.assertThat(result).isEqualTo(files)
    }

    @Test
    fun check_gatherTemplateStrings_single_file() {
//        // Given:
//        val gatheredStringsFile = getGatheredStringFile("strings_1.json")
//        val placeholderTemplateFile = temporaryFolder.newFile("the_template_placeholder.json")
//        val expectedGatheredTemplates = getTemplatesFile("templates_1.json").readText()
//        every {
//            filesProvider.getGatheredStringsFile()
//        } returns gatheredStringsFile
//        every { filesProvider.getTemplateFile() } returns placeholderTemplateFile
//        every {
//            resourcesHandler.getTemplatesFromFile(placeholderTemplateFile)
//        } returns StringsTemplatesModel("", listOf(), mapOf())
//        every { filesProvider.getAllGatheredStringsFiles() } returns listOf(gatheredStringsFile)
//
//        // When:
//        gatherTemplatesAction.gatherTemplateStrings()
//
//        // Then:
//        verify { resourcesHandler.saveTemplates(any(), eq(placeholderTemplateFile)) }
//        Truth.assertThat(placeholderTemplateFile.readText()).isEqualTo(expectedGatheredTemplates)
    }

    @Test
    fun check_gatherTemplateStrings_old_equals_new() {
        // Given:
        val gatheredStringsFile = getGatheredStringFile("strings_1.json")
        val placeholderTemplateFile = getTemplatesFile("templates_1.json")
        every {
            filesProvider.getGatheredStringsFile()
        } returns gatheredStringsFile
        every { filesProvider.getTemplateFile() } returns placeholderTemplateFile
        every { filesProvider.getAllGatheredStringsFiles() } returns listOf(gatheredStringsFile)

        // When:
        gatherTemplatesAction.gatherTemplateStrings()

        // Then:
//        verify(exactly = 0) { resourcesHandler.saveTemplates(any(), any()) }
    }

    @Test
    fun check_gatherTemplateStrings_multiple_files() {
//        // Given:
//        val gatheredStringsFile1 = getGatheredStringFile("strings_1.json")
//        val placeholderTemplateFile1 = temporaryFolder.newFile("the_template_placeholder1.json")
//        val expectedGatheredTemplates1 = getTemplatesFile("templates_1.json").readText()
//
//        val gatheredStringsFile2 = getGatheredStringFile("strings_1_es.json")
//        val placeholderTemplateFile2 = temporaryFolder.newFile("the_template_placeholder2.json")
//        val expectedGatheredTemplates2 = getTemplatesFile("templates_1_es.json").readText()
//
//        every {
//            filesProvider.getGatheredStringsFile()
//        } returns gatheredStringsFile1
//        every { filesProvider.getTemplateFile() } returns placeholderTemplateFile1
//        every { filesProvider.getTemplateFile("-es") } returns placeholderTemplateFile2
//
//        every {
//            resourcesHandler.getTemplatesFromFile(placeholderTemplateFile1)
//        } returns StringsTemplatesModel("", listOf(), mapOf())
//        every {
//            resourcesHandler.getTemplatesFromFile(placeholderTemplateFile2)
//        } returns StringsTemplatesModel("", listOf(), mapOf())
//
//        every { filesProvider.getAllGatheredStringsFiles() } returns listOf(gatheredStringsFile1, gatheredStringsFile2)
//
//        // When:
//        gatherTemplatesAction.gatherTemplateStrings()
//
//        // Then:
//        verify { resourcesHandler.saveTemplates(any(), eq(placeholderTemplateFile1)) }
//        verify { resourcesHandler.saveTemplates(any(), eq(placeholderTemplateFile2)) }
//        Truth.assertThat(placeholderTemplateFile1.readText()).isEqualTo(expectedGatheredTemplates1)
//        Truth.assertThat(placeholderTemplateFile2.readText()).isEqualTo(expectedGatheredTemplates2)
    }

    private fun getGatheredStringFile(fileName: String): File {
        return File(javaClass.getResource("gatherTemplates/gathered_string_files/$fileName").file)
    }

    private fun getTemplatesFile(fileName: String): File {
        return File(javaClass.getResource("gatherTemplates/templates_files/$fileName").file)
    }
}