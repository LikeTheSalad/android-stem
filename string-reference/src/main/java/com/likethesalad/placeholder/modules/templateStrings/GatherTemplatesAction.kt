package com.likethesalad.placeholder.modules.templateStrings

import com.likethesalad.placeholder.modules.common.Constants
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.data.AndroidResourceType
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.api.collection.ResourceCollection
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourcesProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class GatherTemplatesAction @AssistedInject constructor(
    @Assisted private val androidVariantContext: AndroidVariantContext
) {

    @AssistedFactory
    interface Factory {
        fun create(androidVariantContext: AndroidVariantContext): GatherTemplatesAction
    }

    private val resourcesHandler = androidVariantContext.androidResourcesHandler

    fun gatherTemplateStrings(
        outputDir: File,
        commonResources: ResourcesProvider,
        templateResources: ResourcesProvider
    ) {
        val commonHandler = commonResources.resources
        val templatesHandler = templateResources.resources
        for (language in commonHandler.listLanguages()) {
            val resources = commonHandler.getMergedResourcesForLanguage(language)
            val templates = templatesHandler.getMergedResourcesForLanguage(language)
            resourcesHandler.saveTemplates(outputDir, gatheredStringsToTemplateStrings(language, resources, templates))
        }
    }

    private fun gatheredStringsToTemplateStrings(
        language: Language,
        resources: ResourceCollection,
        templates: ResourceCollection
    ): StringsTemplatesModel {
        val stringResources = asStringResources(resources)
        val stringTemplates = asStringResources(templates)
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