package com.likethesalad.android.templates.common.tasks.identifier.action

import com.likethesalad.android.protos.StringResource
import com.likethesalad.android.protos.ValuesStringResources
import com.likethesalad.android.resources.StringResourceCollector
import com.likethesalad.android.resources.extensions.get
import com.likethesalad.android.resources.extensions.name
import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItemsSerializer2
import java.io.File

class TemplatesIdentifierAction2 private constructor(
    private val localResDirs: List<Collection<File>>,
    private val outputFile: File,
    private val configuration: StemConfiguration
) {

    companion object {
        fun create(
            stemConfiguration: StemConfiguration,
            localResources: List<Collection<File>>,
            outputFile: File
        ): TemplatesIdentifierAction2 {
            return TemplatesIdentifierAction2(localResources, outputFile, stemConfiguration)
        }
    }

    fun execute() {
        val templates = getTemplatesIdsFromResources()
        outputFile.writeText(TemplateItemsSerializer2.serialize(templates))
    }

    private fun getTemplatesIdsFromResources(): List<String> {
        val stringCollection = StringResourceCollector.collectStringResourcesPerValueDir(localResDirs)
        return if (configuration.searchForTemplatesInLanguages()) {
            getTemplatesFromAllCollections(stringCollection)
        } else {
            val mainLanguageResources = stringCollection.get("values")
            getTemplatesForCollection(mainLanguageResources)
        }
    }

    private fun getTemplatesFromAllCollections(stringCollection: ValuesStringResources): List<String> {
        val templates = mutableSetOf<String>()

        stringCollection.values.forEach { (_, strings) ->
            val collectionTemplates = getTemplatesForCollection(strings.strings)
            templates.addAll(collectionTemplates)
        }

        return templates.toList()
    }

    private fun getTemplatesForCollection(stringResources: List<StringResource>?): List<String> {
        if (stringResources == null) {
            return emptyList()
        }

        val templates = filterTemplates(stringResources)

        return templates.map {
            it.name()
        }.sorted()
    }

    private fun filterTemplates(stringResources: Collection<StringResource>): List<StringResource> {
        return stringResources.filter { stringResource ->
            configuration.placeholderRegex.containsMatchIn(stringResource.text)
        }
    }
}