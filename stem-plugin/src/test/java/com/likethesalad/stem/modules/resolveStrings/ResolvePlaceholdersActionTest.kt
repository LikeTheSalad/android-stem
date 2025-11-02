package com.likethesalad.stem.modules.resolveStrings

import com.likethesalad.android.protos.Attribute
import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.modules.common.helpers.resources.ResourcesHandler
import com.likethesalad.stem.modules.resolveStrings.resolver.TemplateResolver
import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.stem.testutils.named
import com.likethesalad.tools.resource.api.android.environment.Language
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.File
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ResolvePlaceholdersActionTest {

    private lateinit var resourcesHandler: ResourcesHandler
    private lateinit var templateResolver: TemplateResolver

    private lateinit var resolvePlaceholdersAction: ResolvePlaceholdersAction

    @BeforeEach
    fun setUp() {
        resourcesHandler = mockk(relaxUnitFun = true)
        templateResolver = mockk(relaxUnitFun = true)

        resolvePlaceholdersAction = ResolvePlaceholdersAction(
            templateResolver, resourcesHandler
        )
    }

    @Test
    fun check_resolve() {
        // Given:
        val templatesDir = mockk<File>()
        val outputDir = mockk<File>()
        val templatesFile = mockk<File>()
        val templates = mockk<StringsTemplatesModel>()
        val language = Language.Default
        val expectedResult = listOf<StringResource>(mockk(), mockk())
        every { templates.language }.returns(language)
        every { templatesDir.listFiles() }.returns(listOf(templatesFile).toTypedArray())
        every { resourcesHandler.getTemplatesFromFile(templatesFile) } returns templates
        every { templateResolver.resolveTemplates(templates) }.returns(expectedResult)

        // When:
        resolvePlaceholdersAction.resolve(templatesDir, outputDir)

        // Then:
        verify { resourcesHandler.saveResolvedStringList(outputDir, expectedResult, language) }
    }

    @Test
    fun check_resolve_language_strings_for_language_values() {
        // Given:
        val templatesDir = mockk<File>()
        val outputDir = mockk<File>()
        val templatesFile = mockk<File>()
        val language = Language.Custom("es")
        val templates = mockk<StringsTemplatesModel>()
        val translatableStrings = getStringsList(3, true)
        val nonTranslatableStrings = getStringsList(2, false)
        val allStrings = translatableStrings + nonTranslatableStrings
        every { templates.language }.returns(language)
        every { templatesDir.listFiles() }.returns(listOf(templatesFile).toTypedArray())
        every { resourcesHandler.getTemplatesFromFile(templatesFile) } returns templates
        every { templateResolver.resolveTemplates(templates) }.returns(allStrings)

        // When:
        resolvePlaceholdersAction.resolve(templatesDir, outputDir)

        // Then:
        verify { resourcesHandler.saveResolvedStringList(outputDir, translatableStrings, language) }
    }

    @Test
    fun check_resolve_language_strings_for_main_values() {
        // Given:
        val templatesDir = mockk<File>()
        val outputDir = mockk<File>()
        val templatesFile = mockk<File>()
        val language = Language.Default
        val templates = mockk<StringsTemplatesModel>()
        val translatableStrings = getStringsList(3, true)
        val nonTranslatableStrings = getStringsList(2, false)
        val allStrings = translatableStrings + nonTranslatableStrings
        every { templates.language }.returns(language)
        every { templatesDir.listFiles() }.returns(listOf(templatesFile).toTypedArray())
        every { resourcesHandler.getTemplatesFromFile(templatesFile) } returns templates
        every { templateResolver.resolveTemplates(templates) }.returns(allStrings)

        // When:
        resolvePlaceholdersAction.resolve(templatesDir, outputDir)

        // Then:
        verify { resourcesHandler.saveResolvedStringList(outputDir, allStrings, language) }
    }

    private fun getStringsList(
        count: Int, translatable: Boolean
    ): List<StringResource> {
        val strings = mutableListOf<StringResource>()
        for (it in 0 until count) {
            val string = StringResource.named(
                "name_$it", "value_$it",
                listOf(
                    Attribute("translatable", translatable.toString(), null)
                )
            )
            strings.add(string)
        }

        return strings
    }
}