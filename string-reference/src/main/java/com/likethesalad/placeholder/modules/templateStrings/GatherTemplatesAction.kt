package com.likethesalad.placeholder.modules.templateStrings

import com.likethesalad.android.string.resources.locator.extractor.StringXmlResourceExtractor
import com.likethesalad.placeholder.modules.common.Constants
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.templateStrings.collector.TemplatesResourceSourceProvider
import com.likethesalad.placeholder.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.data.AndroidResourceType
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.api.collection.ResourceCollection
import com.likethesalad.tools.resource.collector.android.AndroidResourceCollector
import com.likethesalad.tools.resource.locator.android.extension.LanguageResourceFinder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class GatherTemplatesAction @AssistedInject constructor(
    @Assisted private val androidVariantContext: AndroidVariantContext,
    private val templatesResourceSourceProviderFactory: TemplatesResourceSourceProvider.Factory
) {

    @AssistedFactory
    interface Factory {
        fun create(androidVariantContext: AndroidVariantContext): GatherTemplatesAction
    }

    private val resourcesHandler = androidVariantContext.androidResourcesHandler
    private val templatesDirHandler = androidVariantContext.templatesDirHandler

    fun gatherTemplateStrings(outputDir: File, languageResourceFinder: LanguageResourceFinder) {
        for (language in languageResourceFinder.listLanguages()) {
            val resources = languageResourceFinder.getMergedResourcesForLanguage(language)
            resourcesHandler.saveTemplates(outputDir, gatheredStringsToTemplateStrings(language, resources))
        }
    }

    fun getTemplatesSourceFiles(): List<File> {
        return templatesDirHandler.templatesDirs.map { it.dir }
    }

    private fun getTemplateStringResources(): List<StringAndroidResource> {
        val sourceProvider = templatesResourceSourceProviderFactory.create(templatesDirHandler.templatesDirs)
        val extractor = StringXmlResourceExtractor()
        val collector =
            AndroidResourceCollector.newInstance(sourceProvider, androidVariantContext.variantTree, extractor)

        return asStringResources(collector.collect())
    }

    private fun gatheredStringsToTemplateStrings(
        language: Language,
        resources: ResourceCollection
    ): StringsTemplatesModel {
        val stringResources = asStringResources(resources)
        val stringTemplates = getTemplateStringResources()
        val placeholdersResolved = getPlaceholdersResolved(stringResources, stringTemplates)

        return StringsTemplatesModel(
            language,
            stringTemplates,
            placeholdersResolved
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun asStringResources(resources: ResourceCollection): List<StringAndroidResource> {
        return resources.getResourcesByType(AndroidResourceType.StringType) as List<StringAndroidResource>
    }

    private fun getPlaceholdersResolved(
        strings: List<StringAndroidResource>,
        templates: List<StringAndroidResource>
    ): Map<String, String> {
        val stringsMap = stringResourcesToMap(strings)
        val placeholders = templates.map { Constants.PLACEHOLDER_REGEX.findAll(it.stringValue()) }
            .flatMap { it.toList().map { m -> m.groupValues[1] } }.toSet()

        val placeholdersResolved = mutableMapOf<String, String>()

        for (it in placeholders) {
            placeholdersResolved[it] = stringsMap.getValue(it)
        }

        return placeholdersResolved
    }

    private fun stringResourcesToMap(list: Collection<StringAndroidResource>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (it in list) {
            map[it.name()] = it.stringValue()
        }
        return map
    }
}