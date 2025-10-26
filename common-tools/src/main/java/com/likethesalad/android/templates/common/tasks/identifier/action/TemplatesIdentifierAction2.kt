package com.likethesalad.android.templates.common.tasks.identifier.action

import com.likethesalad.android.resources.StringResourceCollector
import com.likethesalad.android.resources.data.StringResource
import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItemsSerializer2
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
        val templates = getTemplatesIdsFromResources()
        outputFile.writeText(TemplateItemsSerializer2.serialize(templates))
    }

    private fun getTemplatesIdsFromResources(): List<String> {
        val stringCollection = StringResourceCollector.collectStringResourcesPerValueDir(localResDirs, ignoreResDir)
        return if (configuration.searchForTemplatesInLanguages()) {
            getTemplatesFromAllCollections(stringCollection)
        } else {
            val mainLanguageResources = stringCollection["values"]
            getTemplatesForCollection(mainLanguageResources)
        }
    }

    private fun getTemplatesFromAllCollections(stringCollection: Map<String, Collection<StringResource>>): List<String> {
        val templates = mutableSetOf<String>()

        stringCollection.forEach { (_, strings) ->
            val collectionTemplates = getTemplatesForCollection(strings)
            templates.addAll(collectionTemplates)
        }

        return templates.toList()
    }

    private fun getTemplatesForCollection(stringResources: Collection<StringResource>?): List<String> {
        if (stringResources == null) {
            return emptyList()
        }

        val templates = filterTemplates(stringResources)

        return templates.map {
            it.getName()
        }.sorted()
    }

    private fun filterTemplates(stringResources: Collection<StringResource>): List<StringResource> {
        return stringResources.filter { stringResource ->
            configuration.placeholderRegex.containsMatchIn(stringResource.value)
        }
    }
}