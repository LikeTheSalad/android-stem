package com.likethesalad.placeholder.tasks.actions

import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.common.helpers.resources.ResourcesHandler
import com.likethesalad.placeholder.modules.templateStrings.GatherTemplatesAction
import com.likethesalad.placeholder.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.AndroidResourceScope
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.environment.Variant
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.api.collection.BasicResourceCollection
import com.likethesalad.tools.resource.api.collection.ResourceCollection
import com.likethesalad.tools.resource.locator.android.extension.LanguageResourceFinder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.io.File

class GatherTemplatesActionTest {

    private lateinit var resourcesHandler: ResourcesHandler
    private lateinit var gatherTemplatesAction: GatherTemplatesAction
    private lateinit var languageResourceFinder: LanguageResourceFinder

    @Before
    fun setUp() {
        val androidVariantContext = mockk<AndroidVariantContext>()
        resourcesHandler = mockk(relaxUnitFun = true)
        languageResourceFinder = mockk()

        every { androidVariantContext.androidResourcesHandler }.returns(resourcesHandler)

        gatherTemplatesAction = GatherTemplatesAction(
            androidVariantContext
        )
    }

    @Test
    fun check_gatherTemplateStrings_single_file() {
//        // Given:
        val language1 = Language.Default
        val language2 = Language.Custom("es")
        val (gatheredStrings, expectedGatheredTemplates) = getRawAndTemplatesPair(
            language1
        )
        val (gatheredStringsEs, expectedGatheredTemplatesEs) = getRawAndTemplatesPair(
            language2
        )
        val outputDir = mockk<File>()
        every { languageResourceFinder.listLanguages() }.returns(listOf(language1, language2))
        every {
            languageResourceFinder.getMergedResourcesForLanguage(language1)
        }.returns(gatheredStrings)
        every {
            languageResourceFinder.getMergedResourcesForLanguage(language2)
        }.returns(gatheredStringsEs)

        // When:
        gatherTemplatesAction.gatherTemplateStrings(outputDir, languageResourceFinder)

        // Then:
        verify { resourcesHandler.saveTemplates(outputDir, expectedGatheredTemplates) }
        verify { resourcesHandler.saveTemplates(outputDir, expectedGatheredTemplatesEs) }
    }

    private fun getRawAndTemplatesPair(
        language: Language
    ): Pair<ResourceCollection, StringsTemplatesModel> {
        val scope = AndroidResourceScope(Variant.Default, language)
        val gatheredStrings =
            BasicResourceCollection(
                listOf(
                    StringAndroidResource(
                        "app_name",
                        "TesT",
                        scope
                    ),
                    StringAndroidResource(
                        "other_string",
                        "Random string",
                        scope
                    ),
                    StringAndroidResource(
                        "template_welcome",
                        "The welcome message for \${app_name}",
                        scope
                    ),
                    StringAndroidResource(
                        mapOf(
                            "name" to "template_message_non_translatable",
                            "translatable" to "false"
                        ), "Non translatable \${app_name}",
                        scope
                    ),
                    StringAndroidResource(
                        "template_this_contains_template",
                        "This is the welcome: \${template_welcome}",
                        scope
                    )
                )
            )
        val expectedGatheredTemplates =
            StringsTemplatesModel(
                language,
                listOf(
                    StringAndroidResource(
                        "template_welcome",
                        "The welcome message for \${app_name}",
                        scope
                    ),
                    StringAndroidResource(
                        mapOf(
                            "name" to "template_message_non_translatable",
                            "translatable" to "false"
                        ), "Non translatable \${app_name}",
                        scope
                    ),
                    StringAndroidResource(
                        "template_this_contains_template",
                        "This is the welcome: \${template_welcome}",
                        scope
                    )
                ), mapOf(
                    "app_name" to "TesT",
                    "template_welcome" to "The welcome message for \${app_name}"
                )
            )
        return Pair(gatheredStrings, expectedGatheredTemplates)
    }
}