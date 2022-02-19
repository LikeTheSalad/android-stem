package com.likethesalad.stem.tasks.actions

import com.likethesalad.stem.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.stem.modules.common.helpers.resources.ResourcesHandler
import com.likethesalad.stem.modules.resolveStrings.ResolvePlaceholdersAction
import com.likethesalad.stem.modules.resolveStrings.resolver.TemplateResolver
import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.AndroidResourceScope
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.environment.Variant
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.api.data.AttributeContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.io.File

class ResolvePlaceholdersActionTest {

    private lateinit var resourcesHandler: ResourcesHandler
    private lateinit var templateResolver: TemplateResolver

    private lateinit var resolvePlaceholdersAction: ResolvePlaceholdersAction

    @Before
    fun setUp() {
        val androidVariantContext = mockk<AndroidVariantContext>()
        resourcesHandler = mockk(relaxUnitFun = true)
        templateResolver = mockk(relaxUnitFun = true)

        every { androidVariantContext.androidResourcesHandler }.returns(resourcesHandler)

        resolvePlaceholdersAction = ResolvePlaceholdersAction(
            androidVariantContext, templateResolver
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
        val expectedResult = listOf<StringAndroidResource>(mockk(), mockk())
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
        val translatableStrings = getStringsList(3, true, language)
        val nonTranslatableStrings = getStringsList(2, false, language)
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
        count: Int, translatable: Boolean,
        language: Language = Language.Default,
        scope: AndroidResourceScope = AndroidResourceScope(Variant.Default, language)
    ): List<StringAndroidResource> {
        val strings = mutableListOf<StringAndroidResource>()
        for (it in 0 until count) {
            val string = mockk<StringAndroidResource>()
            val attributes = mockk<AttributeContainer>()
            every { attributes.get("translatable") }.returns(translatable.toString())
            every { string.attributes() }.returns(attributes)
            every { string.getAndroidScope() }.returns(scope)
            strings.add(string)
        }

        return strings
    }
}