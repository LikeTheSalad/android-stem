package com.likethesalad.placeholder.tasks.actions

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.resources.AndroidResourcesHandler
import com.likethesalad.placeholder.data.resources.ResourcesHandler
import com.likethesalad.placeholder.data.storage.FilesProvider
import com.likethesalad.placeholder.resolver.RecursiveLevelDetector
import com.likethesalad.placeholder.resolver.TemplateResolver
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ResolvePlaceholdersActionTest {

    private lateinit var resolvePlaceholdersAction: ResolvePlaceholdersAction
    private lateinit var filesProvider: FilesProvider
    private lateinit var resourcesHandler: ResourcesHandler
    private lateinit var templateResolver: TemplateResolver

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Before
    fun setUp() {
        filesProvider = mockk()
//        resourcesHandler = spyk(AndroidResourcesHandler(filesProvider))
        templateResolver = spyk(TemplateResolver(RecursiveLevelDetector()))
        resolvePlaceholdersAction = ResolvePlaceholdersAction(filesProvider, resourcesHandler, templateResolver)
    }

    @Test
    fun check_getTemplatesFiles() {
        // Given:
        val templatesFiles = listOf<File>(mockk())
        every { filesProvider.getAllTemplatesFiles() } returns templatesFiles

        // When:
        val result = resolvePlaceholdersAction.getTemplatesFiles()

        // Then:
        verify { filesProvider.getAllTemplatesFiles() }
        Truth.assertThat(result).isEqualTo(templatesFiles)
    }

    @Test
    fun check_getResolvedFiles() {
        // Given:
        val resolvedFiles = listOf<File>(mockk())
        every { filesProvider.getAllExpectedResolvedFiles() } returns resolvedFiles

        // When:
        val result = resolvePlaceholdersAction.getResolvedFiles()

        // Then:
        verify { filesProvider.getAllExpectedResolvedFiles() }
        Truth.assertThat(result).isEqualTo(resolvedFiles)
    }

    @Test
    fun check_resolve() {
        // Given:
        val template = getTemplateFile("the_templates_1.json")
        val resultPlaceholderFile = temporaryFolder.newFile()
        val expectedResult = getResolvedFile("strings_resolved_1.xml").readText()
        every { filesProvider.getAllTemplatesFiles() } returns listOf(template)
        every { filesProvider.getResolvedFile("") } returns resultPlaceholderFile

        // When:
        resolvePlaceholdersAction.resolve()

        // Then:
        verify { resourcesHandler.getTemplatesFromFile(template) }
        verify { templateResolver.resolveTemplates(any()) }
        verify { resourcesHandler.saveResolvedStringList(any(), any()) }
        Truth.assertThat(resultPlaceholderFile.readText()).isEqualTo(expectedResult)
    }

    @Test
    fun check_resolve_language_strings() {
        // Given:
        val template = getTemplateFile("the_templates_1_es.json")
        val resultPlaceholderFile = temporaryFolder.newFile()
        val expectedResult = getResolvedFile("strings_resolved_1_es.xml").readText()
        every { filesProvider.getAllTemplatesFiles() } returns listOf(template)
        every { filesProvider.getResolvedFile("-es") } returns resultPlaceholderFile

        // When:
        resolvePlaceholdersAction.resolve()

        // Then:
        verify { resourcesHandler.getTemplatesFromFile(template) }
        verify { templateResolver.resolveTemplates(any()) }
        verify { resourcesHandler.saveResolvedStringList(any(), any()) }
        Truth.assertThat(resultPlaceholderFile.readText()).isEqualTo(expectedResult)
    }

    @Test
    fun check_resolve_empty_result() {
        // Given:
        val template = getTemplateFile("the_templates_1.json")
        every { filesProvider.getAllTemplatesFiles() } returns listOf(template)
        every { templateResolver.resolveTemplates(any()) } returns emptyList()
        every { resourcesHandler.removeResolvedStringFileIfExists(any()) } just Runs

        // When:
        resolvePlaceholdersAction.resolve()

        // Then:
        verify { resourcesHandler.getTemplatesFromFile(template) }
        verify { templateResolver.resolveTemplates(any()) }
        verify(exactly = 0) { resourcesHandler.saveResolvedStringList(any(), any()) }
        verify { resourcesHandler.removeResolvedStringFileIfExists("") }
    }

    private fun getTemplateFile(fileName: String): File {
        return File(javaClass.getResource("resolvePlaceholders/templates/$fileName").file)
    }

    private fun getResolvedFile(fileName: String): File {
        return File(javaClass.getResource("resolvePlaceholders/resolved/$fileName").file)
    }
}