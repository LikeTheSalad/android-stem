package com.likethesalad.placeholder.tasks.actions

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.common.helpers.resources.ResourcesHandler
import com.likethesalad.placeholder.modules.common.helpers.files.storage.FilesProvider
import com.likethesalad.placeholder.modules.resolveStrings.data.helpers.files.ResolvedDataCleaner
import com.likethesalad.placeholder.modules.common.models.PathIdentity
import com.likethesalad.placeholder.modules.common.models.StringResourceModel
import com.likethesalad.placeholder.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.placeholder.modules.resolveStrings.ResolvePlaceholdersAction
import com.likethesalad.placeholder.modules.resolveStrings.data.helpers.files.ResolvedDataCleanerFactory
import com.likethesalad.placeholder.modules.resolveStrings.resolver.TemplateResolver
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.io.File

class ResolvePlaceholdersActionTest {

    private lateinit var filesProvider: FilesProvider
    private lateinit var resourcesHandler: ResourcesHandler
    private lateinit var templateResolver: TemplateResolver
    private lateinit var resolvedDataCleaner: ResolvedDataCleaner

    private lateinit var resolvePlaceholdersAction: ResolvePlaceholdersAction

    @Before
    fun setUp() {
        val androidVariantContext = mockk<AndroidVariantContext>()
        val resolvedDataCleanerFactory = mockk<ResolvedDataCleanerFactory>()
        filesProvider = mockk()
        resourcesHandler = mockk(relaxUnitFun = true)
        templateResolver = mockk(relaxUnitFun = true)
        resolvedDataCleaner = mockk(relaxUnitFun = true)

        every { androidVariantContext.filesProvider }.returns(filesProvider)
        every { androidVariantContext.androidResourcesHandler }.returns(resourcesHandler)
        every { resolvedDataCleanerFactory.create(androidVariantContext) }.returns(resolvedDataCleaner)

        resolvePlaceholdersAction = ResolvePlaceholdersAction(
            androidVariantContext, resolvedDataCleanerFactory, templateResolver
        )
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
        val templatesFile = mockk<File>()
        val pathIdentity = mockk<PathIdentity>()
        val templates = mockk<StringsTemplatesModel>()
        val expectedResult = listOf<StringResourceModel>(mockk(), mockk())
        every { pathIdentity.suffix }.returns("")
        every { templates.pathIdentity }.returns(pathIdentity)
        every { filesProvider.getAllTemplatesFiles() } returns listOf(templatesFile)
        every { resourcesHandler.getTemplatesFromFile(templatesFile) } returns templates
        every { templateResolver.resolveTemplates(templates) }.returns(expectedResult)

        // When:
        resolvePlaceholdersAction.resolve()

        // Then:
        verify { resolvedDataCleaner.removeResolvedFiles() }
        verify { resourcesHandler.saveResolvedStringList(expectedResult, pathIdentity) }
    }

    @Test
    fun check_resolve_language_strings_for_language_values() {
        // Given:
        val templatesFile = mockk<File>()
        val pathIdentity = mockk<PathIdentity>()
        val templates = mockk<StringsTemplatesModel>()
        val translatableStrings = getStringsList(3, true)
        val nonTranslatableStrings = getStringsList(2, false)
        val allStrings = translatableStrings + nonTranslatableStrings
        every { pathIdentity.suffix }.returns("-es")
        every { templates.pathIdentity }.returns(pathIdentity)
        every { filesProvider.getAllTemplatesFiles() } returns listOf(templatesFile)
        every { resourcesHandler.getTemplatesFromFile(templatesFile) } returns templates
        every { templateResolver.resolveTemplates(templates) }.returns(allStrings)

        // When:
        resolvePlaceholdersAction.resolve()

        // Then:
        verify { resolvedDataCleaner.removeResolvedFiles() }
        verify { resourcesHandler.saveResolvedStringList(translatableStrings, pathIdentity) }
    }

    @Test
    fun check_resolve_language_strings_for_main_values() {
        // Given:
        val templatesFile = mockk<File>()
        val pathIdentity = mockk<PathIdentity>()
        val templates = mockk<StringsTemplatesModel>()
        val translatableStrings = getStringsList(3, true)
        val nonTranslatableStrings = getStringsList(2, false)
        val allStrings = translatableStrings + nonTranslatableStrings
        every { pathIdentity.suffix }.returns("")
        every { templates.pathIdentity }.returns(pathIdentity)
        every { filesProvider.getAllTemplatesFiles() } returns listOf(templatesFile)
        every { resourcesHandler.getTemplatesFromFile(templatesFile) } returns templates
        every { templateResolver.resolveTemplates(templates) }.returns(allStrings)

        // When:
        resolvePlaceholdersAction.resolve()

        // Then:
        verify { resolvedDataCleaner.removeResolvedFiles() }
        verify { resourcesHandler.saveResolvedStringList(allStrings, pathIdentity) }
    }

    private fun getStringsList(count: Int, translatable: Boolean): List<StringResourceModel> {
        val strings = mutableListOf<StringResourceModel>()
        for (it in 0 until count) {
            val string = mockk<StringResourceModel>()
            every { string.translatable }.returns(translatable)
            strings.add(string)
        }

        return strings
    }
}