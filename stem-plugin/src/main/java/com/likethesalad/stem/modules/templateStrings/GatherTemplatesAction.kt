package com.likethesalad.stem.modules.templateStrings

import com.likethesalad.android.protos.StringResource
import com.likethesalad.android.protos.ValuesStringResources
import com.likethesalad.stem.configuration.StemConfiguration
import com.likethesalad.stem.modules.common.helpers.resources.ResourcesHandler
import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.stem.tools.extensions.get
import com.likethesalad.stem.tools.extensions.name
import java.io.File

class GatherTemplatesAction(
    private val resourcesHandler: ResourcesHandler,
    private val stemConfiguration: StemConfiguration
) {
    companion object {
        private val VALUES_REGEX = Regex("values(?:-(.+))*")
    }

    fun gatherTemplateStrings(
        outputDir: File,
        stringValues: ValuesStringResources
    ) {
        val templateIds = getTemplatesIdsFromResources(stringValues)

        if (templateIds.isEmpty()) {
            return
        }

        stringValues.values.forEach { (valueDirName, strings) ->
            val suffix = getSuffix(valueDirName)
            val templates = getTemplatesFromResources(templateIds, strings.strings)
            val localizedStrings = strings.strings
            val stringResources = if (suffix.isEmpty()) {
                listOf(localizedStrings)
            } else {
                stringValues.get("values")?.let { listOf(localizedStrings, it) } ?: listOf(localizedStrings)
            }
            resourcesHandler.saveTemplates(
                outputDir,
                gatheredStringsToTemplateStrings(suffix, stringResources, templates)
            )
        }
    }

    private fun getSuffix(valuesDirName: String): String {
        val match = VALUES_REGEX.matchEntire(valuesDirName)
            ?: throw IllegalArgumentException("Invalid values dir name: $valuesDirName")

        return match.groupValues[1]
    }

    private fun getTemplatesFromResources(
        templateIds: List<String>,
        resources: Collection<StringResource>
    ): List<StringResource> {
        return resources.filter {
            it.name() in templateIds
        }
    }

    private fun getTemplatesIdsFromResources(stringValues: ValuesStringResources): List<String> {
        return if (stemConfiguration.searchForTemplatesInLanguages()) {
            getTemplatesFromAllCollections(stringValues)
        } else {
            val mainLanguageResources = stringValues.get("values")
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
            stemConfiguration.placeholderRegex.containsMatchIn(stringResource.text)
        }
    }

    private fun gatheredStringsToTemplateStrings(
        suffix: String,
        stringResources: List<List<StringResource>>,
        stringTemplates: List<StringResource>
    ): StringsTemplatesModel {
        val placeholdersResolved = getPlaceholdersResolved(stringResources, stringTemplates)

        return StringsTemplatesModel(
            suffix,
            stringTemplates,
            placeholdersResolved
        )
    }

    private fun getPlaceholdersResolved(
        strings: List<List<StringResource>>,
        templates: List<StringResource>
    ): Map<String, String> {
        val unresolvedPlaceholders = templates.map { stemConfiguration.placeholderRegex.findAll(it.text) }
            .flatMap { it.toList().map { m -> m.groupValues[1] } }.distinct().toMutableList()

        val placeholdersResolved = mutableMapOf<String, String>()

        for (list in strings) {
            for (string in list) {
                val name = string.name()
                if (name in unresolvedPlaceholders) {
                    placeholdersResolved[name] = string.text
                    unresolvedPlaceholders.remove(name)
                    if (unresolvedPlaceholders.isEmpty()) {
                        break
                    }
                }
            }
        }

        if (unresolvedPlaceholders.isNotEmpty()) {
            throw IllegalStateException("Could not find the following placeholder values: $unresolvedPlaceholders")
        }

        return placeholdersResolved
    }
}