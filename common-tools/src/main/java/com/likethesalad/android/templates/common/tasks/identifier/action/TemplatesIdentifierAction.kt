package com.likethesalad.android.templates.common.tasks.identifier.action

import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.android.templates.common.plugins.extension.StemExtension
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItem
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItemsSerializer
import com.likethesalad.android.templates.common.utils.CommonConstants
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.impl.AndroidResourceType
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.api.collection.ResourceCollection
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourcesProvider
import java.io.File

class TemplatesIdentifierAction private constructor(
    private val localResources: ResourcesProvider,
    private val outputFile: File,
    private val configuration: StemConfiguration
) {

    companion object {
        fun create(
            stemExtension: StemExtension,
            localResources: ResourcesProvider,
            outputFile: File
        ): TemplatesIdentifierAction {
            return TemplatesIdentifierAction(localResources, outputFile, StemConfiguration(stemExtension))
        }
    }

    fun execute() {
        val templates = getTemplatesFromResources()
        outputFile.writeText(TemplateItemsSerializer.serialize(templates))
    }

    private fun getTemplatesFromResources(): List<TemplateItem> {
        return if (configuration.searchForTemplatesInLanguages()) {
            getTemplatesFromAllCollections()
        } else {
            val mainLanguageResources = localResources.resources.getMergedResourcesForLanguage(Language.Default)
            getTemplatesForCollection(mainLanguageResources)
        }
    }

    private fun getTemplatesFromAllCollections(): List<TemplateItem> {
        val resources = localResources.resources
        val allLanguages = resources.listLanguages()
        val templates = mutableSetOf<TemplateItem>()

        allLanguages.forEach { language ->
            val collection = resources.getMergedResourcesForLanguage(language)
            val collectionTemplates = getTemplatesForCollection(collection)
            templates.addAll(collectionTemplates)
        }

        return templates.toList()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getTemplatesForCollection(resourceCollection: ResourceCollection): List<TemplateItem> {
        val stringResources = resourceCollection.getResourcesByType(AndroidResourceType.StringType)
        val templates = filterTemplates(stringResources as List<StringAndroidResource>)

        return templates.sortedBy { it.name() }.map {
            TemplateItem(it.name(), it.type().getName())
        }
    }

    private fun filterTemplates(stringResources: List<StringAndroidResource>): List<StringAndroidResource> {
        return stringResources.filter { stringResource ->
            CommonConstants.PLACEHOLDER_REGEX.containsMatchIn(stringResource.stringValue())
        }
    }
}