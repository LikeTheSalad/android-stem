package com.likethesalad.stem.modules.templateStrings

import com.likethesalad.android.protos.StringResource
import com.likethesalad.android.protos.ValuesStringResources
import com.likethesalad.android.resources.extensions.name
import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItemsSerializer2
import com.likethesalad.stem.modules.common.helpers.resources.ResourcesHandler
import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.environment.Language
import java.io.File

class GatherTemplatesAction2(
    private val resourcesHandler: ResourcesHandler,
    private val stemConfiguration: StemConfiguration
) {
    private val VALUES_REGEX = Regex("values(?:-(.+))*")

    fun gatherTemplateStrings(
        outputDir: File,
        stringValues: ValuesStringResources,
        templateIdsContainer: File
    ) {
        val templateIds = getTemplateIds(templateIdsContainer)

        if (templateIds.isEmpty()) {
            return
        }

        stringValues.values.forEach { (valueDirName, strings) ->
            val language = getLanguage(valueDirName)
            val templates = getTemplatesFromResources(templateIds, strings.strings)
            val resources = strings.strings.minus(templates)
            resourcesHandler.saveTemplates(outputDir, gatheredStringsToTemplateStrings(language, resources, templates))
        }
    }

    private fun getLanguage(valuesDirName: String): Language {
        val match = VALUES_REGEX.matchEntire(valuesDirName)
            ?: throw IllegalArgumentException("Invalid values dir name: $valuesDirName")

        val languagePart = match.groupValues[1]
        if (languagePart.isEmpty()) {
            return Language.Default
        }
        return Language.Custom(languagePart)
    }

    private fun getTemplatesFromResources(
        templateIds: List<String>,
        resources: Collection<StringResource>
    ): List<StringResource> {
        return resources.filter {
            it.name() in templateIds
        }
    }

    private fun getTemplateIds(localTemplateIdsContainer: File): List<String> {
//        val templateIdsFromDependencies = getTemplateIdsFromDependencies() //todo delete
        return TemplateItemsSerializer2.deserialize(localTemplateIdsContainer.readText())
    }


//    Todo rework
//    private fun getTemplateIdsFromDependencies(): List<TemplateItem> {
//        val templatesProviders = TemplatesProviderLoader.load(templatesProviderJarsFinder.templateProviderJars)
//        return templatesProviders.map { TemplateItemsSerializer.deserialize(it.getTemplates()) }.flatten()
//    }

    private fun gatheredStringsToTemplateStrings(
        language: Language,
        stringResources: List<StringResource>,
        stringTemplates: List<StringResource>
    ): StringsTemplatesModel {
        val placeholdersResolved = getPlaceholdersResolved(stringResources, stringTemplates)

        return StringsTemplatesModel(
            language,
            stringTemplates,
            placeholdersResolved
        )
    }

    private fun getPlaceholdersResolved(
        strings: List<StringResource>,
        templates: List<StringResource>
    ): Map<String, String> {
        val stringsMap = stringResourcesToMap(strings)
        val placeholders = templates.map { stemConfiguration.placeholderRegex.findAll(it.text) }
            .flatMap { it.toList().map { m -> m.groupValues[1] } }.toSet()

        val placeholdersResolved = mutableMapOf<String, String>()

        for (it in placeholders) {
            placeholdersResolved[it] = stringsMap.getValue(it)
        }

        return placeholdersResolved
    }

    private fun stringResourcesToMap(list: List<StringResource>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (it in list) {
            map[it.name()] = it.text
        }
        return map
    }
}