package com.likethesalad.placeholder.tasks.actions

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.common.helpers.files.storage.FilesProvider
import com.likethesalad.placeholder.modules.common.helpers.resources.ResourcesHandler
import com.likethesalad.placeholder.modules.resolveStrings.ResolvePlaceholdersAction
import com.likethesalad.placeholder.modules.resolveStrings.resolver.TemplateResolver
import com.likethesalad.placeholder.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.api.data.AttributeContainer
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

    private lateinit var resolvePlaceholdersAction: ResolvePlaceholdersAction

    @Before
    fun setUp() {
        val androidVariantContext = mockk<AndroidVariantContext>()
        filesProvider = mockk()
        resourcesHandler = mockk(relaxUnitFun = true)
        templateResolver = mockk(relaxUnitFun = true)

        every { androidVariantContext.filesProvider }.returns(filesProvider)
        every { androidVariantContext.androidResourcesHandler }.returns(resourcesHandler)

        resolvePlaceholdersAction = ResolvePlaceholdersAction(
            androidVariantContext, templateResolver
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
        val templates = mockk<StringsTemplatesModel>()
        val language = Language.Default
        val expectedResult = listOf<StringAndroidResource>(mockk(), mockk())
        every { templates.language }.returns(language)
        every { filesProvider.getAllTemplatesFiles() } returns listOf(templatesFile)
        every { resourcesHandler.getTemplatesFromFile(templatesFile) } returns templates
        every { templateResolver.resolveTemplates(templates) }.returns(expectedResult)

        // When:
        resolvePlaceholdersAction.resolve()

        // Then:
        verify { resourcesHandler.saveResolvedStringList(expectedResult, language) }
    }

    @Test
    fun check_resolve_language_strings_for_language_values() {
        // Given:
        val templatesFile = mockk<File>()
        val language = Language.Custom("es")
        val templates = mockk<StringsTemplatesModel>()
        val translatableStrings = getStringsList(3, true)
        val nonTranslatableStrings = getStringsList(2, false)
        val allStrings = translatableStrings + nonTranslatableStrings
        every { templates.language }.returns(language)
        every { filesProvider.getAllTemplatesFiles() } returns listOf(templatesFile)
        every { resourcesHandler.getTemplatesFromFile(templatesFile) } returns templates
        every { templateResolver.resolveTemplates(templates) }.returns(allStrings)

        // When:
        resolvePlaceholdersAction.resolve()

        // Then:
        verify { resourcesHandler.saveResolvedStringList(translatableStrings, language) }
    }

    @Test
    fun check_resolve_language_strings_for_main_values() {
        // Given:
        val templatesFile = mockk<File>()
        val language = Language.Default
        val templates = mockk<StringsTemplatesModel>()
        val translatableStrings = getStringsList(3, true)
        val nonTranslatableStrings = getStringsList(2, false)
        val allStrings = translatableStrings + nonTranslatableStrings
        every { templates.language }.returns(language)
        every { filesProvider.getAllTemplatesFiles() } returns listOf(templatesFile)
        every { resourcesHandler.getTemplatesFromFile(templatesFile) } returns templates
        every { templateResolver.resolveTemplates(templates) }.returns(allStrings)

        // When:
        resolvePlaceholdersAction.resolve()

        // Then:
        verify { resourcesHandler.saveResolvedStringList(allStrings, language) }
    }

    private fun getStringsList(count: Int, translatable: Boolean): List<StringAndroidResource> {
        val strings = mutableListOf<StringAndroidResource>()
        for (it in 0 until count) {
            val string = mockk<StringAndroidResource>()
            val attributes = mockk<AttributeContainer>()
            every { attributes.get("translatable") }.returns(translatable.toString())
            every { string.attributes() }.returns(attributes)
            strings.add(string)
        }

        return strings
    }
}