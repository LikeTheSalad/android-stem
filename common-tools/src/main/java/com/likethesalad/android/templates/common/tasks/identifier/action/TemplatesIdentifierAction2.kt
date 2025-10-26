package com.likethesalad.android.templates.common.tasks.identifier.action

import com.likethesalad.android.resources.StringResourceCollector
import com.likethesalad.android.resources.data.StringResource
import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItem
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItemsSerializer
import java.io.File

class TemplatesIdentifierAction2 private constructor(
    private val localResDirs: List<Collection<File>>,
    // Todo shouldn't be needed to filter dirs if the last variant (upmost) is removed from inputs.
    private val ignoreResDir: (File) -> Boolean,
    private val outputFile: File,
    private val configuration: StemConfiguration
) {

    companion object {
        fun create(
            stemConfiguration: StemConfiguration,
            localResources: List<Collection<File>>,
            filterResDir: (File) -> Boolean,
            outputFile: File
        ): TemplatesIdentifierAction2 {
            return TemplatesIdentifierAction2(localResources, filterResDir, outputFile, stemConfiguration)
        }
    }

    fun execute() {
        val templates = getTemplatesFromResources()
        outputFile.writeText(TemplateItemsSerializer.serialize(templates))
    }

    private fun getTemplatesFromResources(): List<TemplateItem> {
        val stringCollection = StringResourceCollector.collectStringResourcesPerValueDir(localResDirs, ignoreResDir)
        return if (configuration.searchForTemplatesInLanguages()) {
            getTemplatesFromAllCollections(stringCollection)
        } else {
            val mainLanguageResources = stringCollection["values"]
            getTemplatesForCollection(mainLanguageResources)
        }
    }

    private fun getTemplatesFromAllCollections(stringCollection: Map<String, Collection<StringResource>>): List<TemplateItem> {
        val templates = mutableSetOf<TemplateItem>()

        stringCollection.forEach { (_, strings) ->
            val collectionTemplates = getTemplatesForCollection(strings)
            templates.addAll(collectionTemplates)
        }

        return templates.toList()
    }

    private fun getTemplatesForCollection(stringResources: Collection<StringResource>?): List<TemplateItem> {
        if (stringResources == null) {
            return emptyList()
        }

        val templates = filterTemplates(stringResources)

        return templates.sortedBy { it.getName() }.map {
            TemplateItem(it.getName(), "string")
        }
    }

    private fun filterTemplates(stringResources: Collection<StringResource>): List<StringResource> {
        return stringResources.filter { stringResource ->
            configuration.placeholderRegex.containsMatchIn(stringResource.value)
        }
    }
}